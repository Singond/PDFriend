#!/bin/sh

# Make the launcher executable
chmod 555 pdfriend

# Setup PATH
cat << 'EOF' > /etc/profile.d/pdfriend.sh
# Add PDFriend installation directory to PATH
export "PATH=$PATH:%{INSTALL_PATH}"
EOF
