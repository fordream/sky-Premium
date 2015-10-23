package z.lib.base;

import org.json.JSONObject;

import android.util.Log;

public class CheerzServiceCallBack {
	/**
 * 
 */
	// public void onInitSuccess() {
	//
	// }

	/**
* 
*/
	// public void onInitFail() {
	//
	// }

	/**
 * 
 */
	public void onStart() {

	}

	/**
	 * 
	 * @param message
	 */
	public void onError(String message) {

	}

	/**
	 * 
	 * @param response
	 */
	public void onSucces(String response) {

	}

	/**
	 * 
	 * @param jsonObject
	 */
	public void onSucces(JSONObject jsonObject) {

	}
	
	public void onErrorAccountConflic(String respone){
		Log.i("TAG", "TAG");
	}
	
	public void onErrorTimeOut(String respone){
		
	}
	
}