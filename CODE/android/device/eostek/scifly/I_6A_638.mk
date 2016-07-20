# aosp_pitaya prodct.
#
# This cleanly combines a set of product-specific configuration.

# Inherit from hardware-specific part of the product configuration.
$(call inherit-product, device/eostek/scifly/device.mk)
# Inherit from the common Open Source product configuration.
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/aosp_base.mk)
$(call inherit-product-if-exists, vendor/google/products/gms.mk)

# Set those variables here to overwrite the inherited values.
PRODUCT_NAME := I_6A_638
PRODUCT_DEVICE := scifly
PRODUCT_BRAND := eostek
PRODUCT_MODEL := EosTek Android TV
PRODUCT_MANUFACTURER := EosTek
