!bin/bash

echo "======================================= ARXAN BUILD START =======================================\n"
echo "current directory : $PWD"

# Environment variable setting
export ANDROID_SDK_ROOT=~/Library/Android/sdk

# Arxan input setting
SECURE_DEX=/Applications/A4A_6.4.1/bin/secure-dex
input_aab=../app/build/outputs/bundle/realRelease/QRPayforShop-1.2.8-real-release.aab
output_dir=./output
blueprint=./mpm_blueprint.json

#bundletool path
bundletool=/Applications/A4A_6.4.1/bundletool-all-1.14.1.jar

# keystore values
keystore_path=../BC_QR_KeyStore.jks
keystore_alias=bc_qrcode
keystore_pass=bccard1587

rm -rf ${output_dir}

${SECURE_DEX} -b ${blueprint} -i ${input_aab} -o ${output_dir}

fileextension='.aab'
filename=$(basename -s ${fileextension} ${input_aab})

echo "file name : $filename"

protected_aab=${output_dir}/${filename}-unaligned-unsigned-protected${fileextension}

signed_aab=${output_dir}/${filename}-protected${fileextension}
signed_apks=${output_dir}/${filename}-protected.apks

mv ${protected_aab} ${signed_aab}

java -jar ${bundletool} build-apks --bundle=${signed_aab} --output=${signed_apks} --ks=${keystore_path} --ks-pass=pass:${keystore_pass} --ks-key-alias=${keystore_alias} --key-pass=pass:${keystore_pass} --mode=universal

jarsigner -sigalg SHA256withRSA -digestalg SHA-256 -keystore ${keystore_path} -storepass ${keystore_pass} ${signed_aab} ${keystore_alias}
