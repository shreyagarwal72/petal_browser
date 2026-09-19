#!/usr/bin/env python3
"""
Strips pre-existing META-INF signature files from an Android App Bundle (.aab)
so that subsequent jarsigner signing enforces exactly 1 certificate chain,
as required by the Google Play Store.
"""
import os
import sys
import zipfile

def strip_signatures(aab_path):
    if not os.path.isfile(aab_path):
        print(f"Error: {aab_path} not found")
        sys.exit(1)
    temp_path = aab_path + '.clean'
    with zipfile.ZipFile(aab_path, 'r') as zin:
        with zipfile.ZipFile(temp_path, 'w', compression=zipfile.ZIP_DEFLATED) as zout:
            for item in zin.infolist():
                fn = item.filename
                # Exclude signature files from META-INF
                if fn.startswith('META-INF/') and any(fn.endswith(ext) for ext in ['.SF', '.RSA', '.DSA', '.EC', '.MF']):
                    continue
                zout.writestr(item, zin.read(fn))
    os.replace(temp_path, aab_path)
    print(f"Successfully cleaned pre-existing signatures from {aab_path}")

if __name__ == '__main__':
    if len(sys.argv) > 1:
        strip_signatures(sys.argv[1])
    else:
        print("Usage: strip_aab_signatures.py <path_to_aab>")
        sys.exit(1)
