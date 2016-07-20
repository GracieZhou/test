#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
//do not remove the line below
//--- include mmap layout (mmap.ini) , specified by make teeImage(param3) -> mmapGenerator.py -> mmapDBGen.cpp ------------
#include "_mmap.ini"
#include "MMAPInfo.h"

#define SGROUPCOUNT 8

//do not remove the line below
//--- target mmap database filename (mmapDB.ini), specified by make teeImage(param2) -> mmapGenerator.py -> mmapDBGen.cpp ------------
#define MMAP_TARGET_FILENAME "/home/fenoss.hu/H638/I_6A638_02/vendor/mstar/supernova/target/dvb.monet/tmp_image/config/mmapDB.ini"

typedef struct
{
    unsigned char valid;
    unsigned long addr;
    unsigned long length;
    unsigned char MiuSel;  
    unsigned long index;
} ST_MMAP_DB_ITEM;

typedef struct
{
    unsigned long addr;
    unsigned long length;
} ST_MMAP_GROUP_ITEM;

#define TOTAL_MMAP_ID (E_MMAP_ID_MAX+1)
ST_MMAP_DB_ITEM        mmapDB[TOTAL_MMAP_ID];
ST_MMAP_GROUP_ITEM     mmapGroup[SGROUPCOUNT+1];

unsigned long miu2_interval=0xffffffff;
unsigned long miu1_interval=0xffffffff;

void writeToFile()
{
    FILE *fp = fopen(MMAP_TARGET_FILENAME, "w");
    int i = 0;
    unsigned long sGroupCount = SGROUPCOUNT;
    unsigned long mmapCount = TOTAL_MMAP_ID;
    if (fp==NULL)
    {
        printf("fopen error: %s\n", strerror(errno));
        exit(1);
    }

    //write SGROUPCOUNT & E_MMAP_ID_MAX
    // unsigned long is 32 bits in build server 55, but 64 bits in build server 28
    // cannot use sizeof(unsigned long) or sizeof(unsigned int), because they are machine dependent
    // force to write 4 bytes to file
    if (fwrite( &(sGroupCount), 4, 1, fp) < 0)
    {
        printf("fwrite gropu start addr error: %s\n", strerror(errno));
        exit(1);
    }
    if (fwrite( &(mmapCount), 4, 1, fp) < 0)
    {
        printf("fwrite gropu start addr error: %s\n", strerror(errno));
        exit(1);
    }

    //write mmapGropu
    for ( i = 1 ; i <= SGROUPCOUNT; i++)
    {
        if (fwrite( &(mmapGroup[i].addr), 4, 1, fp) < 0)
        {
            printf("fwrite gropu start addr error: %s\n", strerror(errno));
            exit(1);
        }
        if (fwrite( &(mmapGroup[i].length), 4, 1, fp) < 0)
        {
            printf("fwrite gropu len error: %s\n", strerror(errno));
            exit(1);
        }        
    }

    //write mmapDB
    for ( i = 0 ; i < TOTAL_MMAP_ID; i++)
    {
#if 0
        if (fprintf( fp, "%u ", mmapDB[i].valid) < 0)
        {
            printf("fprintf valid field error: %s\n", strerror(errno));
            exit(1);
        }
        if (fprintf( fp, "%lx ", mmapDB[i].addr) < 0)
        {
            printf("fprintf addr field error: %s\n", strerror(errno));
            exit(1);            
        }
        if (fprintf( fp, "%lx ", mmapDB[i].length) < 0)
        {
            printf("fprintf len field error: %s\n", strerror(errno));
            exit(1);            
        }
        if (fprintf( fp, "%u ", mmapDB[i].MiuSel) < 0)
        {
            printf("fprintf valid field error: %s\n", strerror(errno));
            exit(1);
        }
        if (fprintf( fp, "%u\n", mmapDB[i].index) < 0)
        {
            printf("fprintf valid field error: %s\n", strerror(errno));
            exit(1);
        }
#else
        if (fwrite( &(mmapDB[i].valid), sizeof(unsigned char), 1, fp) < 0)
        {
            printf("fwrite valid field error: %s\n", strerror(errno));
            exit(1);
        }
        if (fwrite( &(mmapDB[i].addr), 4, 1, fp) < 0)
        {
            printf("fwrite addr field error: %s\n", strerror(errno));
            exit(1);
        }
        if (fwrite( &(mmapDB[i].length), 4, 1, fp) < 0)
        {
            printf("fwrite length field error: %s\n", strerror(errno));
            exit(1);
        }
        if (fwrite( &(mmapDB[i].MiuSel), sizeof(unsigned char), 1, fp) < 0)
        {
            printf("fwrite MiuSelfield error: %s\n", strerror(errno));
            exit(1);
        }
        if (fwrite( &(mmapDB[i].index), 4, 1, fp) < 0)
        {
            printf("fwrite index field error: %s\n", strerror(errno));
            exit(1);
        }
#endif
    }
    fclose(fp);
}

int main()
{
#if defined(MIU_INTERVAL)
    miu1_interval=MIU_INTERVAL;
#endif
#if defined(MIU_INTERVAL2)
    miu2_interval=MIU_INTERVAL2;
#endif
    //do not remove the line below
    //------------ mmap database auto gen start ------------

    mmapDB[E_DFB_JPD_WRITE].valid = 1;
    mmapDB[E_DFB_JPD_WRITE].addr = E_DFB_JPD_WRITE_ADR;
    mmapDB[E_DFB_JPD_WRITE].length = E_DFB_JPD_WRITE_LEN;
    mmapDB[E_DFB_JPD_WRITE].MiuSel = 0;

    mmapDB[E_MMAP_ID_MAD_R2].valid = 1;
    mmapDB[E_MMAP_ID_MAD_R2].addr = E_MMAP_ID_MAD_R2_ADR;
    mmapDB[E_MMAP_ID_MAD_R2].length = E_MMAP_ID_MAD_R2_LEN;
    mmapDB[E_MMAP_ID_MAD_R2].MiuSel = 0;

    mmapDB[E_MMAP_ID_NUTTX_MEM].valid = 1;
    mmapDB[E_MMAP_ID_NUTTX_MEM].addr = E_MMAP_ID_NUTTX_MEM_ADR;
    mmapDB[E_MMAP_ID_NUTTX_MEM].length = E_MMAP_ID_NUTTX_MEM_LEN;
    mmapDB[E_MMAP_ID_NUTTX_MEM].MiuSel = 0;

    mmapDB[E_MMAP_ID_HW_AES_BUF].valid = 1;
    mmapDB[E_MMAP_ID_HW_AES_BUF].addr = E_MMAP_ID_HW_AES_BUF_ADR;
    mmapDB[E_MMAP_ID_HW_AES_BUF].length = E_MMAP_ID_HW_AES_BUF_LEN;
    mmapDB[E_MMAP_ID_HW_AES_BUF].MiuSel = 0;

    mmapDB[E_SECURE_SHM].valid = 1;
    mmapDB[E_SECURE_SHM].addr = E_SECURE_SHM_ADR;
    mmapDB[E_SECURE_SHM].length = E_SECURE_SHM_LEN;
    mmapDB[E_SECURE_SHM].MiuSel = 0;

    mmapDB[E_SECURE_UPDATE_AREA].valid = 1;
    mmapDB[E_SECURE_UPDATE_AREA].addr = E_SECURE_UPDATE_AREA_ADR;
    mmapDB[E_SECURE_UPDATE_AREA].length = E_SECURE_UPDATE_AREA_LEN;
    mmapDB[E_SECURE_UPDATE_AREA].MiuSel = 0;

    mmapDB[E_MMAP_ID_VDEC_CPU].valid = 1;
    mmapDB[E_MMAP_ID_VDEC_CPU].addr = E_MMAP_ID_VDEC_CPU_ADR;
    mmapDB[E_MMAP_ID_VDEC_CPU].length = E_MMAP_ID_VDEC_CPU_LEN;
    mmapDB[E_MMAP_ID_VDEC_CPU].MiuSel = 1;

    mmapDB[E_MMAP_ID_VDEC_SHARE_MEM].valid = 1;
    mmapDB[E_MMAP_ID_VDEC_SHARE_MEM].addr = E_MMAP_ID_VDEC_SHARE_MEM_ADR;
    mmapDB[E_MMAP_ID_VDEC_SHARE_MEM].length = E_MMAP_ID_VDEC_SHARE_MEM_LEN;
    mmapDB[E_MMAP_ID_VDEC_SHARE_MEM].MiuSel = 1;

    mmapDB[E_MMAP_ID_VDEC_BITSTREAM].valid = 1;
    mmapDB[E_MMAP_ID_VDEC_BITSTREAM].addr = E_MMAP_ID_VDEC_BITSTREAM_ADR;
    mmapDB[E_MMAP_ID_VDEC_BITSTREAM].length = E_MMAP_ID_VDEC_BITSTREAM_LEN;
    mmapDB[E_MMAP_ID_VDEC_BITSTREAM].MiuSel = 1;

    mmapDB[E_MMAP_ID_VDEC_BITSTREAM_SD].valid = 1;
    mmapDB[E_MMAP_ID_VDEC_BITSTREAM_SD].addr = E_MMAP_ID_VDEC_BITSTREAM_SD_ADR;
    mmapDB[E_MMAP_ID_VDEC_BITSTREAM_SD].length = E_MMAP_ID_VDEC_BITSTREAM_SD_LEN;
    mmapDB[E_MMAP_ID_VDEC_BITSTREAM_SD].MiuSel = 1;

    mmapDB[E_MMAP_ID_VDEC_MVC_BITSTREAM].valid = 1;
    mmapDB[E_MMAP_ID_VDEC_MVC_BITSTREAM].addr = E_MMAP_ID_VDEC_MVC_BITSTREAM_ADR;
    mmapDB[E_MMAP_ID_VDEC_MVC_BITSTREAM].length = E_MMAP_ID_VDEC_MVC_BITSTREAM_LEN;
    mmapDB[E_MMAP_ID_VDEC_MVC_BITSTREAM].MiuSel = 1;

    mmapDB[E_MMAP_ID_DIP_MEM].valid = 1;
    mmapDB[E_MMAP_ID_DIP_MEM].addr = E_MMAP_ID_DIP_MEM_ADR;
    mmapDB[E_MMAP_ID_DIP_MEM].length = E_MMAP_ID_DIP_MEM_LEN;
    mmapDB[E_MMAP_ID_DIP_MEM].MiuSel = 1;


    mmapDB[E_MMAP_ID_NUTTX_MEM].index = 1;
    mmapGroup[1].addr = E_MMAP_ID_NUTTX_MEM_ADR;
    mmapGroup[1].length = E_MMAP_ID_NUTTX_MEM_LEN;




    mmapDB[E_MMAP_ID_VDEC_CPU].index = 3;
    mmapGroup[3].addr = E_MMAP_ID_VDEC_CPU_ADR + MIU_INTERVAL;
    mmapGroup[3].length = E_MMAP_ID_VDEC_CPU_LEN;




    mmapDB[E_MMAP_ID_HW_AES_BUF].index = 5;
    mmapGroup[5].addr = E_MMAP_ID_HW_AES_BUF_ADR;
    mmapGroup[5].length = E_MMAP_ID_HW_AES_BUF_LEN;




    //------------ mmap database auto gen end ------------
    //put the miu_interval in mmapDB[0]
    mmapDB[E_MIU1_INTERVAL].valid = 0xFF;
    mmapDB[E_MIU1_INTERVAL].addr = miu1_interval;
    mmapDB[E_MIU1_INTERVAL].length = 0xFFFFFFFF;
    mmapDB[E_MIU1_INTERVAL].MiuSel = 0xFF;
    
    mmapDB[E_MIU2_INTERVAL].valid = 0xFF;
    mmapDB[E_MIU2_INTERVAL].addr = miu2_interval;
    mmapDB[E_MIU2_INTERVAL].length = 0xFFFFFFFF;
    mmapDB[E_MIU2_INTERVAL].MiuSel = 0xFF;

    mmapDB[E_MMAP_ID_MAX].valid = 0xFF;
    mmapDB[E_MMAP_ID_MAX].addr = 0xFFFFFFFF;
    mmapDB[E_MMAP_ID_MAX].length = 0xFFFFFFFF;
    mmapDB[E_MMAP_ID_MAX].MiuSel = 0xFF;
    writeToFile();

    return 0;
}

