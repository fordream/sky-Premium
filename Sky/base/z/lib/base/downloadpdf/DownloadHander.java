package z.lib.base.downloadpdf;

import z.lib.base.callback.RestClient.IDownloadUploadFileCallBack;
import android.os.Handler;
import android.os.Message;

public abstract class DownloadHander extends Handler {
	public static Message createMessageError(String url, String messageStr) {
		Message message = new Message();
		message.what = IDownloadUploadFileCallBack.STATUS_DOWNLOAD_UPLOAD_FAIL;
		message.obj = new String[] { url, messageStr };
		return message;
	}

	public static Message createMessageSuccess(String url, String messageStr) {
		Message message = new Message();
		message.what = IDownloadUploadFileCallBack.STATUS_SUCESS;
		message.obj = new String[] { url, messageStr };
		return message;
	}

	@Override
	public void dispatchMessage(Message msg) {
		super.dispatchMessage(msg);

		if (msg.what == IDownloadUploadFileCallBack.STATUS_SUCESS) {
			onSuccess(((String[]) msg.obj)[0]);
		} else if (msg.what == IDownloadUploadFileCallBack.STATUS_DOWNLOAD_UPLOAD_FAIL) {
			onFail(((String[]) msg.obj)[0], ((String[]) msg.obj)[1]);
		}
	}

	public abstract void onSuccess(String url);

	public abstract void onFail(String url, String message);
}