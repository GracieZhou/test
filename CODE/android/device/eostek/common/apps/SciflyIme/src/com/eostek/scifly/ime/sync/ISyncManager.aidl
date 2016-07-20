package com.eostek.scifly.ime.sync;

import com.eostek.scifly.ime.sync.words.IWordsSyncManager;
import com.eostek.scifly.ime.sync.sensors.ISensorSyncManager;

interface ISyncManager{

	IWordsSyncManager getWordsSyncManager();
	
	ISensorSyncManager getSensorSyncManager();

}