#include "MstarNative.h"

#include "apiSWI2C.h"
#include "drvGPIO.h"
#include "drvSAR.h"

#define KEYPAD_IIC_SLADE_ID 	8<<1
#define SCL_GPIO_PORT			45
#define SDA_GPIO_PORT			44
#define KEY_FLAG_SAR_PORT 		39

#define BUTTON_1	1
#define BUTTON_2	2
#define BUTTON_3	4
#define BUTTON_4	8
#define BUTTON_5	16
#define BUTTON_6	32
#define BUTTON_7	64
#define BUTTON_8	128
#define BUTTON_9	256

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
	iic_init();
    return JNI_VERSION_1_4;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved)
{
}

void iic_init()
{
	//global init
	MDrv_SYS_GlobalInit();
	
	//gpio init
	mdrv_gpio_init();
	
	//software i2c init
	SWI2C_BusCfg swBusCfg = {SCL_GPIO_PORT,
				SDA_GPIO_PORT,
				75};			
	MApi_SWI2C_Init(&swBusCfg, 1);
	
	mdrv_gpio_set_input(KEY_FLAG_SAR_PORT);
}

JNIEXPORT jint JNICALL Java_com_utsmta_mstar_MstarNative_scan_1touch_1pad_1key(JNIEnv *env, jclass jcls)
{
	//printf("====Java_com_utsmta_mstar_MtaJNI_scan_1key====\n");
	int key = -1;
	if(mdrv_gpio_get_level(KEY_FLAG_SAR_PORT)){
		printf("mta mdrv_gpio_get_level true\n");
		MS_U8 iicBuffer[5] = {0, 0, 0, 0, 0};
		if(MApi_SWI2C_ReadBytes(KEYPAD_IIC_SLADE_ID, 0, NULL, 5, iicBuffer) && iicBuffer[3] == 0xaa){
			key = iicBuffer[0] ;
			//printf("mta key before convert is:%d\n",key);
			key = convert2keycode(key);
			//printf("mta key after convert is:%d\n",key);
		}
	}
	return key;
}

int convert2keycode(int key)
{
	int keycode = 0;
	switch(key){
		case BUTTON_1:
			keycode = 1;
			break;
		case BUTTON_2:
			keycode = 2;
			break;
		case BUTTON_3:
			keycode = 3;
			break;
		case BUTTON_4:
			keycode = 4;
			break;
		case BUTTON_5:
			keycode = 5;
			break;
		case BUTTON_6:
			keycode = 6;
			break;
		case BUTTON_7:
			keycode = 7;
			break;
		case BUTTON_8:
			keycode = 8;
			break;	
		case BUTTON_9:
			keycode = 9;
			break;	
		default:
			keycode = 0;
			break;
	}
	
	return keycode;
}