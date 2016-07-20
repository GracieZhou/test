DEVICE=kikat
PRODUCT_OUT=`pwd`
PARTITION_SIZE=0x800000
MOUNT_POINT=system
PARTITION_NAME=tvdatabase
echo "Make image file(tvdatabase.img)"
#
# use this shell, you must put the tool "make_ext4fs" to the system's sbin
#
./tools/make_ext4fs -t -l $PARTITION_SIZE -a tvdatabase $PRODUCT_OUT/images/tvdatabase.img $PRODUCT_OUT/$PARTITION_NAME
