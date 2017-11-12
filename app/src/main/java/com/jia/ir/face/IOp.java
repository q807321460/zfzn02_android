package com.jia.ir.face;


import com.jia.ir.db.ETDB;

public interface IOp {
	public void Load(ETDB db);
	public void Update(ETDB db);
	public void Delete(ETDB db);
	public void Inster(ETDB db);
	public int GetCount();
	public Object GetItem(int i);
}
