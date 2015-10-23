package z.lib.base;

import android.os.Handler;
import android.os.Message;

public abstract class SkyLoader {

	public final void executeLoader(final long time) {

		new Thread(new Runnable() {
			private Object object;
			private Handler handler = new Handler() {
				@Override
				public void dispatchMessage(Message msg) {
					super.dispatchMessage(msg);
					loadSucess(object);
				}
			};

			@Override
			public void run() {

				try {
					Thread.sleep(time);
				} catch (Exception e) {
				}
				object = loadData();
				handler.sendEmptyMessage(0);
			}

		}).start();
	}

	public abstract Object loadData();

	public abstract void loadSucess(Object data);
}