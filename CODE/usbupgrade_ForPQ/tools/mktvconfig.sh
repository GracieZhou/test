DEVICE=kikat
PRODUCT_OUT=`pwd`
PARTITION_SIZE=0xA00000
MOUNT_POINT=system
PARTITION_NAME=tvconfig
echo "Make image file(tvconfig.img)"
#
# use this shell, you must put the tool "make_ext4fs" to the system's sbin
#
./tools/make_ext4fs -t -l $PARTITION_SIZE -a tvconfig $PRODUCT_OUT/images/tvconfig.img $PRODUCT_OUT/$PARTITION_NAME
