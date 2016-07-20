
package com.eostek.tvmenu.picture;

public class VideoDataSet {
    public static enum EN_MS_PICTURE {
        // picture mode dynamic
        PICTURE_DYNAMIC,
        // picture mode normal
        PICTURE_NORMAL,
        // picture mode mild
        PICTURE_SOFT,
        // picture mode user
        PICTURE_USER,
        // picture mode vivid
        PICTURE_VIVID,
        // picture mode natural
        PICTURE_NATURAL,
        // picture mode sports
        PICTURE_SPORTS,
        // picture mode number
        PICTURE_NUMS
    }

    /** color temperature */
    public static enum EN_MS_COLOR_TEMP {
        // color temperature cool
        MS_COLOR_TEMP_COOL,
        // color temperature medium
        MS_COLOR_TEMP_NATURE,
        // color temperature warm
        MS_COLOR_TEMP_WARM,
        // color temperature user
        MS_COLOR_TEMP_USER,
        // color temperature number
        MS_COLOR_TEMP_NUM
    }

    /** define detail setting of picture mode */
    public static class T_MS_PICTURE {
        // backlight
        public short backlight;

        // contrast
        public short contrast;

        // brightness
        public short brightness;

        // Saturation
        public short saturation;

        // Sharpness
        public short sharpness;

        // Hue
        public short hue;

        // color temperature setting
        public EN_MS_COLOR_TEMP eColorTemp;

        public T_MS_PICTURE(short backlight, short con, short bri, short sat, short sha, short hue,
                EN_MS_COLOR_TEMP colortemp) {
            this.backlight = backlight;
            this.contrast = con;
            this.brightness = bri;
            this.saturation = sat;
            this.sharpness = sha;
            this.hue = hue;
            this.eColorTemp = colortemp;
        }
    } // T_MS_PICTURE

    /** define noise reduction setting */
    public static enum EN_MS_NR {
        // noise reduction off
        MS_NR_OFF,
        // noise reduction low
        MS_NR_LOW,
        // noise reduction middle
        MS_NR_MIDDLE,
        // noise reduction high
        MS_NR_HIGH,
        // noise reduction auto
        MS_NR_AUTO,
        // total noise reduction type number
        MS_NR_NUM,
    }

    /** MPEG noise reduction setting */
    public static enum EN_MS_MPEG_NR {
        // / MPEG noise reduction off
        MS_MPEG_NR_OFF,
        // / MPEG noise reduction low
        MS_MPEG_NR_LOW,
        // / MPEG noise reduction middle
        MS_MPEG_NR_MIDDLE,
        // / MPEG noise reduction high
        MS_MPEG_NR_HIGH,
        // / total mpeg noise reduction type number
        MS_MPEG_NR_NUM,
    }

    /** define enum for noise reduction and mpeg noise reduction */
    public static class T_MS_NR_MODE {
        // / noise reduction setting
        public EN_MS_NR eNR;

        // / MPEG noise reduction setting
        public EN_MS_MPEG_NR eMPEG_NR;

        public T_MS_NR_MODE(EN_MS_NR evalue1, EN_MS_MPEG_NR evalue2) {
            this.eNR = evalue1;
            this.eMPEG_NR = evalue2;
        }
    }

    /** define video setting for */
    // public class VideoParam
    // //{
    // / check sum <<checksum should be put at top of the struct, do not
    // move it to other place>>
    public int CheckSum;

    // / picture mode setting
    public EN_MS_PICTURE ePicture;

    // / picture mode detail setting, 24Byte
    public T_MS_PICTURE astPicture[];

    // / enum for noise reduction and mpeg noise reduction
    public T_MS_NR_MODE eNRMode[];
    // }
}