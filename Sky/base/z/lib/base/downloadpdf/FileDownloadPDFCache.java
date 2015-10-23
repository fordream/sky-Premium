package z.lib.base.downloadpdf;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import z.lib.base.callback.RestClient;
import z.lib.base.callback.RestClient.IDownloadUploadFileCallBack;
import android.content.Context;

public class FileDownloadPDFCache {
	private static FileDownloadPDFCache instance;

	public static FileDownloadPDFCache getInstance(Context context) {
		if (instance == null) {
			instance = new FileDownloadPDFCache(context);
		}

		instance.updateContext(context);
		return instance;
	}

	private void updateContext(Context context) {

	}

	private File cacheDir;

	private FileDownloadPDFCache(Context context) {
		String path = "Android/data/" + context.getPackageName() + "/download";
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), path);
		} else {
			cacheDir = context.getCacheDir();
		}

		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
	}

	public File getFileTemp(String url) {
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		String filename = String.valueOf(url.hashCode());
		File f = new File(cacheDir, filename);
		return f;

	}

	public File getFile(String url) {
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		String filename = String.valueOf(url.hashCode());
		File f = new File(cacheDir, filename + ".pdf");
		return f;

	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		for (File f : files) {
			f.delete();
		}
	}

	/**
	 * 
	 */
	private Map<String, long[]> mapDownloaded = new HashMap<String, long[]>();

	public void clearHistoryDownload() {
		mapDownloaded.clear();
	}

	public void download(final String url, final DownloadHander handler) {
		if (mapDownloaded.containsKey(url)) {
			return;
		}

		mapDownloaded.put(url, new long[] { 0, 0 });

		RestClient client = new RestClient(url);
		client.exeDownloadPDFFile(getFileTemp(url), getFile(url), new IDownloadUploadFileCallBack() {

			@Override
			public void sucess() {
				mapDownloaded.remove(url);
				handler.sendMessage(DownloadHander.createMessageSuccess(url, null));

			}

			@Override
			public void start() {
				mapDownloaded.put(url, new long[] { 0, 0 });
			}

			@Override
			public void onProcess(long total, long curent) {
				mapDownloaded.put(url, new long[] { curent, total });
			}

			@Override
			public void error(int code) {
				mapDownloaded.remove(url);
				handler.sendMessage(DownloadHander.createMessageError(url, null));
			}
		});
	}

	public boolean isDownload(String url) {
		return mapDownloaded.containsKey(url);
	}

	public boolean isDownloaded(String url) {
		return getFile(url).exists();
	}
}