TARGET_EXT4_SIZE=0x00800000
TARGET_NAME=tvdatabase
echo "Make image file($TARGET_NAME.img)"
#
#
./tools/make_ext4fs -S ./tools/file_contexts -l $TARGET_EXT4_SIZE -a $TARGET_NAME ./$TARGET_NAME.img ./$TARGET_NAME
