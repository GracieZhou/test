//#include "wtypes.h"
#include "string.h"

typedef unsigned char       BYTE;

typedef struct _ThreeBytes_
{
	_ThreeBytes_()
	{
	   memset((void*)&byte1,0,1);
	   memset((void*)&byte2,0,1);
	   memset((void*)&byte3,0,1);
	}

	BYTE byte1;
	BYTE byte2;
	BYTE byte3;

}ThreeBytes,*LPThreeBytes;

typedef struct _FourBytes_
{
	_FourBytes_()
	{
	   memset((void*)&byte1,0,1);
	   memset((void*)&byte2,0,1);
	   memset((void*)&byte3,0,1);
	   memset((void*)&byte4,0,1);
	}

	BYTE byte1;
	BYTE byte2;
	BYTE byte3;
	BYTE byte4;

}FourBytes,*LPFourBytes;

char strBase64[] = {"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"};

int Mstar_FindIndex(char c)
{
	for (int i=0; i<64; i++)
	{
	   if (strBase64[i] == c)
	   {
		return i;
	   }
	}
	return -1;
}

//规则转换函数1
void Mstar_Translate3To4(LPThreeBytes pThreeBytes,LPFourBytes pFourBytes)
{
	pFourBytes->byte1 = (pThreeBytes->byte1>>2)&0x3F;
	pFourBytes->byte2 = ((pThreeBytes->byte2>>4)&0x0F)^((pThreeBytes->byte1<<4)&0x30);
	pFourBytes->byte3 = ((((pThreeBytes->byte2<<4)&0xF0)^((pThreeBytes->byte3>>4)&0x0C))>>2)&0x3F;
	pFourBytes->byte4 = pThreeBytes->byte3&0x3F;

	pFourBytes->byte1 = (BYTE)strBase64[pFourBytes->byte1];
	pFourBytes->byte2 = (BYTE)strBase64[pFourBytes->byte2];
	pFourBytes->byte3 = (BYTE)strBase64[pFourBytes->byte3];
	pFourBytes->byte4 = (BYTE)strBase64[pFourBytes->byte4]; 
}

//规则转换函数2
void Mstar_Translate4To3(LPFourBytes pFourBytes,LPThreeBytes pThreeBytes)
{
	FourBytes tempFourBytes;
	//memcpy((void *)&tempFourBytes,pFourBytes,sizeof(FourBytes));
	tempFourBytes.byte1 = (BYTE)Mstar_FindIndex(pFourBytes->byte1);
	tempFourBytes.byte2 = (BYTE)Mstar_FindIndex(pFourBytes->byte2);
	tempFourBytes.byte3 = (BYTE)Mstar_FindIndex(pFourBytes->byte3);
	tempFourBytes.byte4 = (BYTE)Mstar_FindIndex(pFourBytes->byte4);

	pThreeBytes->byte1 = (tempFourBytes.byte1<<2&0xFC)^(tempFourBytes.byte2>>4&0x03);
	pThreeBytes->byte2 = (tempFourBytes.byte2<<4&0xF0)^(tempFourBytes.byte3>>2&0x0F);
	pThreeBytes->byte3 = (tempFourBytes.byte3<<6&0xC0)^(tempFourBytes.byte4&0x3F);
}

//编码函数
void Mstar_EncodeBase64(char *strSource, int nSourceLen, char *strDest)
{
	if (strSource == NULL||nSourceLen<1)
	{
	   return;
	}

	char *pSource = strSource;
	char *pDest = strDest;
	ThreeBytes tempThreeBytes;
	FourBytes tempFourBytes;
	int nPack = nSourceLen/3;
	int nRemainder = nSourceLen%3;

	for (int i=0; i<nPack; i++)
	{
	   memset((void *)&tempThreeBytes,0,sizeof(tempThreeBytes));
	   memset((void *)&tempFourBytes,0,sizeof(tempFourBytes));
	   memcpy((void *)&tempThreeBytes,pSource,sizeof(tempThreeBytes));
	   Mstar_Translate3To4(&tempThreeBytes,&tempFourBytes);
	   memcpy(pDest,(void *)&tempFourBytes,sizeof(tempFourBytes));
	   pSource += sizeof(tempThreeBytes);
	   pDest += sizeof(tempFourBytes);
	}

	if (nRemainder == 0)
	{
	   return;
	}
	else if(nRemainder == 1)
	{
	   memset((void *)&tempThreeBytes,0,sizeof(tempThreeBytes));
	   memset((void *)&tempFourBytes,0,sizeof(tempFourBytes));
	   tempThreeBytes.byte1 = strSource[nSourceLen - 1];
	   tempThreeBytes.byte2 = 0;
	   tempThreeBytes.byte3 = 0;
	   Mstar_Translate3To4(&tempThreeBytes,&tempFourBytes);
	   memcpy(pDest,(void *)&tempFourBytes,sizeof(tempFourBytes));
	   pDest += sizeof(tempFourBytes);
	   pDest -=2;
	   *pDest = '=';
	   pDest++;
	   *pDest = '=';
	}
	else
	{  
	   memset((void *)&tempThreeBytes,0,sizeof(tempThreeBytes));
	   memset((void *)&tempFourBytes,0,sizeof(tempFourBytes));
	   tempThreeBytes.byte1 = strSource[nSourceLen - 2];
	   tempThreeBytes.byte2 = strSource[nSourceLen - 1];
	   tempThreeBytes.byte3 = 0; 
	   Mstar_Translate3To4(&tempThreeBytes,&tempFourBytes);
	   memcpy(pDest,(void *)&tempFourBytes,sizeof(tempFourBytes));
	   pDest += sizeof(tempFourBytes);
	   pDest --;
	   *pDest = '=';
	}
}

//解码函数
void Mstar_DecodeBase64(char *strSource, int nSourceLen, char *strDest)
{
	if (strSource == NULL||nSourceLen<1)
	{
	   return;
	}
	char *pSource = strSource;
	char *pDest = strDest;
	ThreeBytes tempThreeBytes;
	FourBytes tempFourBytes;
	int nPack = nSourceLen/4;
	for (int i=0; i<nPack-1; i++)
	{
	   memset((void *)&tempThreeBytes,0,sizeof(tempThreeBytes));
	   memset((void *)&tempFourBytes,0,sizeof(tempFourBytes));
	   memcpy((void *)&tempFourBytes,pSource,sizeof(tempFourBytes));
	   Mstar_Translate4To3(&tempFourBytes,&tempThreeBytes);
	   memcpy(pDest,(void *)&tempThreeBytes,sizeof(tempThreeBytes));
	   pSource += sizeof(tempFourBytes);
	   pDest += sizeof(tempThreeBytes);
	}
	//有2个等号
	if ((nSourceLen>1)&&(strSource[nSourceLen - 2]=='='))
	{
	   memset((void *)&tempThreeBytes,0,sizeof(tempThreeBytes));
	   memset((void *)&tempFourBytes,0,sizeof(tempFourBytes));
	   memcpy((void *)&tempFourBytes,pSource,sizeof(tempFourBytes));
  
	   tempThreeBytes.byte1 = ((BYTE)Mstar_FindIndex(tempFourBytes.byte1)<<2&0xFC)^((BYTE)Mstar_FindIndex(tempFourBytes.byte2)>>4&0x03);
	   memcpy(pDest,(void *)&tempThreeBytes,1);
	   return;
	}
	//有1个等号
	if (strSource[nSourceLen - 1] == '=')
	{
	   memset((void *)&tempThreeBytes,0,sizeof(tempThreeBytes));
	   memset((void *)&tempFourBytes,0,sizeof(tempFourBytes));
	   memcpy((void *)&tempFourBytes,pSource,sizeof(tempFourBytes));

	   tempThreeBytes.byte1 = ((BYTE)Mstar_FindIndex(tempFourBytes.byte1)<<2&0xFC)^((BYTE)Mstar_FindIndex(tempFourBytes.byte2)>>4&0x03);
	   tempThreeBytes.byte2 = ((BYTE)Mstar_FindIndex(tempFourBytes.byte2)<<4&0xF0)^((BYTE)Mstar_FindIndex(tempFourBytes.byte3)>>2&0x0F);
	   memcpy(pDest,(void *)&tempThreeBytes,2);
	   return;
	}
	//没等号 此时少做了一次循环 再补上
	memset((void *)&tempThreeBytes,0,sizeof(tempThreeBytes));
	memset((void *)&tempFourBytes,0,sizeof(tempFourBytes));
	memcpy((void *)&tempFourBytes,pSource,sizeof(tempFourBytes));
	Mstar_Translate4To3(&tempFourBytes,&tempThreeBytes);
	memcpy(pDest,(void *)&tempThreeBytes,sizeof(tempThreeBytes));
	// pSource += sizeof(tempFourBytes);
	// pDest += sizeof(tempThreeBytes);
}


int maintest()
{
	//printf("编码******************************************\n");
	char strInput[500] = {"娃哈哈"};
	//printf("\"%s\" Base64编码为:",strInput);
	char strOutput[500] = {0};
	Mstar_EncodeBase64(strInput,strlen(strInput),strOutput);
	//printf("%s\n",strOutput);

	//printf("解码******************************************\n");
	memset(strInput,0,sizeof(char)*500);
	//printf("\"%s\" Base64解码为:",strOutput);
	Mstar_DecodeBase64(strOutput,strlen(strOutput),strInput);
	//printf("%s\n",strInput);
	return 0;
}
 

