package com.jia.ir.etclass;

import com.jia.data.DataControl;

import et.song.remote.face.IR;
import et.song.remote.face.IRKeyExValue;
import et.song.remote.face.IRKeyValue;
import et.song.remote.instance.AIR;
import et.song.remote.instance.TV;


public class ETDeviceTV extends ETDevice {
	private DataControl mDC;
	private IR tv = null;
	public ETDeviceTV() {
		tv = new TV();
		for (int i = 0; i < IRKeyValue.TV_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_STUDY);
			key.SetKey(IRKeyValue.TVValue | (i * 2 + 1));
			key.SetDID(0);
			key.SetBrandIndex(0);
			key.SetBrandPos(0);
			key.SetName("");
			key.SetPos(0, 0);
			key.SetRes(0);
			key.SetRow(0);
			SetKey(key);
		}
		for (int i = 0; i < IRKeyExValue.TV_KEYEX_COUNT ; i++){
			ETKeyEx key = new ETKeyEx();
			key.SetKey(IRKeyExValue.TVExValue | (i * 2 + 1));
			try {
				key.SetValue(null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKeyEx(key);
		}
	}
	public ETDeviceTV(int x){
		mDC= DataControl.getInstance();
		setmBrand(x);
		setmIndex(x);
		tv = new TV();
		for (int i = 0; i < 15; i++) {
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_KNOWN);
			key.SetKey(mDC.tvLearnCode[i]);
			key.SetBrandIndex(x);
			key.SetBrandPos(x);
			try {
				key.SetValue(null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKey(key);
		}
	}
//	public ETDeviceTV(int row) {
//		tv = new TV();
//		for (int i = 0; i < IRKeyValue.TV_KEY_COUNT ; i++){
//			ETKey key = new ETKey();
//			key.SetState(ETKey.ETKEY_STATE_TYPE);
//			key.SetKey(IRKeyValue.TVValue | (i * 2 + 1));
//			key.SetRow(row);
//			try {
//				key.SetValue(tv.Search(row, key.GetKey()));
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			SetKey(key);
//		}
//		for (int i = 0; i < IRKeyExValue.TV_KEYEX_COUNT ; i++){
//			ETKeyEx key = new ETKeyEx();
//			key.SetKey(IRKeyExValue.TVExValue | (i * 2 + 1));
//			try {
//				key.SetValue(null);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			SetKeyEx(key);
//		}
//	}
//
	public ETDeviceTV(int row, int col) {
		tv = new TV();
		for (int i = 0; i < IRKeyValue.TV_KEY_COUNT ; i++){
			ETKey key = new ETKey();
			key.SetState(ETKey.ETKEY_STATE_KNOWN);
			key.SetKey(IRKeyValue.TVValue | (i * 2 + 1));
			key.SetBrandIndex(row);
			key.SetBrandPos(col);
			try {
				key.SetValue(tv.Search(row, col, key.GetKey()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKey(key);
		}
		
		for (int i = 0; i < IRKeyExValue.TV_KEYEX_COUNT ; i++){
			ETKeyEx key = new ETKeyEx();
			key.SetKey(IRKeyExValue.TVExValue | (i * 2 + 1));
			try {
				key.SetValue(null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SetKeyEx(key);
		}
	}
	

	
}
