
#echo "Mounting OAD"
#mount -t ubifs ubi:oad /OAD

#echo "Mounting certificate"
mount -t ext4 /dev/mmcblk9 /certificate
#mount -t ubifs ubi:certificate /certificate

#echo "Mounting Customer ini"
mount -t ext4 /dev/mmcblk10 /CustomerBackup
