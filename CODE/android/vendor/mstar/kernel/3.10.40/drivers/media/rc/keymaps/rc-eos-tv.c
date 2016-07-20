/* mstar-tv.h - Keytable for mstar_tv Remote Controller
 *
 * keymap imported from ir-keymaps.c
 *
 * Copyright (c) 2015 by xxh <xxh0312@163.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 */


#include <media/rc-core.h>

#include <linux/module.h>
/*
 * xiaoxuehang
 * this is the remote control that comes with the mstar smart tv
 * which based on STAOS standard.
 */

static struct rc_map_table mstar_tv[] = {
    { 0x0046, KEY_POWER },
    { 0x0050, KEY_0 },
    { 0x0049, KEY_1 },
    { 0x0055, KEY_2 },
    { 0x0059, KEY_3 },
    { 0x004D, KEY_4 },
    { 0x0051, KEY_5 },
    { 0x005D, KEY_6 },
    { 0x0048, KEY_7 },
    { 0x0054, KEY_8 },
    { 0x0058, KEY_9 },
    { 0x0047, KEY_RED },
    { 0x004B, KEY_GREEN },
    { 0x0057, KEY_YELLOW },
    { 0x005B, KEY_BLUE },
    { 0x0052, KEY_UP },
    { 0x0013, KEY_DOWN },
    { 0x0006, KEY_LEFT },
    { 0x001A, KEY_RIGHT },
    { 0x000F, KEY_ENTER },
    { 0x001F, KEY_CHANNELUP },
    { 0x0019, KEY_CHANNELDOWN },
    { 0x0016, KEY_VOLUMEUP },
    { 0x0015, KEY_VOLUMEDOWN },
    { 0x0003, KEY_PAGEUP },
    { 0x0005, KEY_PAGEDOWN },
    { 0x0017, KEY_HOME},
    { 0x0007, KEY_MENU },
    { 0x001B, KEY_BACK },
    { 0x005A, KEY_MUTE },
    { 0x000D, KEY_RECORD },     // DVR
    { 0x0042, KEY_HELP },       // GUIDE
    { 0x0014, KEY_INFO },
    { 0x0040, KEY_KP0 },        // WINDOW
    { 0x0004, KEY_KP1 },        // TV_INPUT
    { 0x000E, KEY_REWIND },
    { 0x0012, KEY_FORWARD },
    { 0x0002, KEY_PREVIOUSSONG },
    { 0x001E, KEY_NEXTSONG },
    { 0x0001, KEY_PLAY },
    { 0x001D, KEY_PAUSE },
    { 0x0011, KEY_STOP },
    { 0x0044, KEY_AUDIO },      // (C)SOUND_MODE
    { 0x0056, KEY_CAMERA },     // (C)PICTURE_MODE
    { 0x004C, KEY_ZOOM },       // (C)ASPECT_RATIO
    { 0x005C, KEY_CHANNEL },    // (C)CHANNEL_RETURN
    { 0x0045, KEY_SLEEP },      // (C)SLEEP
    { 0x004A, KEY_EPG },        // (C)EPG
    { 0x0010, KEY_LIST },       // (C)LIST
    { 0x0053, KEY_SUBTITLE },   // (C)SUBTITLE
    { 0x0041, KEY_FN_F1 },      // (C)MTS
    { 0x004E, KEY_FN_F2 },      // (C)FREEZE
    { 0x000A, KEY_FN_F3 },      // (C)TTX
    { 0x0009, KEY_FN_F4 },      // (C)CC
    { 0x001C, KEY_FN_F5 },      // (C)TV_SETTING
    { 0x0008, KEY_FN_F6 },      // (C)SCREENSHOT
    { 0x000B, KEY_F1 },         // MSTAR_BALANCE
    { 0x0018, KEY_F2 },         // MSTAR_INDEX
    { 0x0000, KEY_F3 },         // MSTAR_HOLD
    { 0x000C, KEY_F4 },         // MSTAR_UPDATE
    { 0x004F, KEY_F5 },         // MSTAR_REVEAL
    { 0x005E, KEY_F6 },         // MSTAR_SUBCODE
    { 0x0043, KEY_F7 },         // MSTAR_SIZE
    { 0x005F, KEY_F8 },         // MSTAR_CLOCK
    { 0x00FE, KEY_POWER2 },     // FAKE_POWER
    { 0x00FF, KEY_OK },         // KEY_OK 
    { 0x0070, KEY_HP },        
    { 0x0071, KEY_CONTEXT_MENU},
    { 0x0060, KEY_F9 },//input source hotkey  // 
    { 0x0061, KEY_F10 }, //sursound  
    { 0x0062, KEY_KP7},//Select channel
    { 0x0063, KEY_KP8},// fav
    { 0x0064, KEY_KP5},//low blue
    { 0x0065, KEY_KP6},//tool 
    { 0x0066, KEY_KP9},//net
    { 0x0072, KEY_F20 },
    { 0x0073, KEY_F21 },
    { 0x0083, KEY_F13 },	//KEY_POWER
    { 0x0084, KEY_F14 },	//KEY_UP
    { 0x0085, KEY_F15 },	//KEY_DOWN
    { 0x0086, KEY_F16 },	//KEY_LEFT
    { 0x0087, KEY_F17 },	//KEY_RIGHT
    { 0x0088, KEY_F18 },	//KEY_ENTER
    { 0x0089, KEY_F19 },	//KEY_BACK
    { 0x0090, KEY_F20 },	//KEY_VOLUMEUP
    { 0x0091, KEY_F21 },	//KEY_VOLUMEDOWN
    { 0x0092, KEY_F22 },	//KEY_CHANNELUP
    { 0x0093, KEY_F23 },	//KEY_CHANNELDOWN
    { 0x0094, KEY_F24 },	//home
    { 0x0095, KEY_EDIT },	//info
    { 0x0021, KEY_A },
    { 0x0022, KEY_B },
    { 0x0023, KEY_C },
    { 0x0024, KEY_D },
    { 0x0025, KEY_E },
    { 0x0026, KEY_F },
    { 0x0027, KEY_G },
    { 0x0028, KEY_H },
    { 0x0029, KEY_I },
    { 0x0030, KEY_J },
    { 0x0031, KEY_K },//KEY_CHANNELBACK
    { 0x0032, KEY_L },//KEY_CHANNELUP
    { 0x0033, KEY_M },//KEY_CHANNELDOWN
    //--------ChangHong IR----------    
    { 0x0034, KEY_VIDEO},         
    { 0x0035, KEY_DVD},         
    { 0x0036, KEY_CLEAR},         
    { 0x0037, KEY_TV2},        
    { 0x0038, KEY_OPTION},        
    { 0x0039, KEY_VCR},        
    { 0x003A, KEY_VCR2},        
    { 0x003B, KEY_SAT},        
    { 0x003C, KEY_SAT2},        
    { 0x003D, KEY_CD},        
    { 0x003E, KEY_TAPE},        
    { 0x003F, KEY_RADIO},        
    { 0x00A0, KEY_TUNER},        
    { 0x00A1, KEY_PLAYER},        
    { 0x00A2, KEY_AUX},        
    { 0x00A3, KEY_MP3},        
    { 0x00A4, KEY_AB}, //no       
    { 0x00A5, KEY_N},        
    { 0x00A6, KEY_O},        
    { 0x00A7, KEY_P},       
    { 0x00A8, KEY_R},        
    { 0x00A9, KEY_S},        
    { 0x00AA, KEY_T},        
    { 0x00AB, KEY_U},        
    { 0x00AC, KEY_V},        
    { 0x00AD, KEY_W},        
    { 0x00AE, KEY_X},        
    { 0x00AF, KEY_Y},        
    { 0x00B0, KEY_NEXT}, //no       
    { 0x00B1, KEY_RESTART},   //no    
    { 0x00B2, KEY_SLOW},   //no     
    { 0x00B3, KEY_SHUFFLE},   //no     
    { 0x00B4, KEY_BREAK},    //no    
    { 0x00B5, KEY_PREVIOUS},    //no    
    { 0x00B6, KEY_DIGITS},   //no     
    { 0x00B7, KEY_F23},        
    { 0x00B8, KEY_F11},        
    { 0x00B9, KEY_F12},        
    { 0x00BA, KEY_KP2},        
    { 0x00BB, KEY_KP3},        
    { 0x00BC, KEY_KP4},        
    { 0x00BD, KEY_FN_F7},   //no    
    { 0x00BE, KEY_FN_F8},    //no    
    { 0x00BF, KEY_FN_F9},        
    { 0x00C1, KEY_FN_F10},        
    { 0x00C2, KEY_FN_F11},        
    { 0x00C3, KEY_FN_F12},        
    { 0x00C4, KEY_FN_D},        
    { 0x00C5, KEY_FN_F},        
    { 0x00C6, KEY_FN_B},        
    { 0x00C7, KEY_TV},        
    { 0x00C8, KEY_PC},        
    { 0x00C9, KEY_KPDOT},        
    { 0x00CA, KEY_KPMINUS},        
    { 0x00CB, KEY_KPPLUS},        
    { 0x00CC, KEY_Z},        
    { 0x00CD, KEY_SELECT},        
    { 0x00CE, KEY_GOTO},
    //----end Changhong --IR-----------	
    //--------Skyworth IR----------    
    { 0x00CF, KEY_FN_E}, //VHF-L
    { 0x00D0, KEY_FN_S}, //VHF-H
    { 0x00D1, KEY_QUESTION}, //UHF
    { 0x00D2, KEY_EMAIL}, //MAC
    { 0x00D3, KEY_CHAT}, //IIC
    { 0x00D4, KEY_SEARCH}, //factory mode
    { 0x00D5, KEY_CONNECT}, //frequent_preset
    { 0x00D6, KEY_FINANCE}, //GINGA
    { 0x00D7, KEY_SPORT}, //10+
    //----end Skyworth IR-----------	

};

static struct rc_map_list mstar_tv_map = {
	.map = {
		.scan    = mstar_tv,
		.size    = ARRAY_SIZE(mstar_tv),
		.rc_type = RC_TYPE_UNKNOWN,	/* Legacy IR type */
		.name    = RC_MAP_MSTAR_TV,
	}
};

static int __init init_rc_map_eos_tv(void)
{
	return rc_map_register(&mstar_tv_map);
}

static void __exit exit_rc_map_eos_tv(void)
{
	rc_map_unregister(&mstar_tv_map);
}

module_init(init_rc_map_eos_tv)
module_exit(exit_rc_map_eos_tv)

MODULE_LICENSE("GPL");
MODULE_AUTHOR("Mauro Carvalho Chehab <xxh0312@163.com>");
