#!/bin/sh

cd %{INSTALL_PATH}

# Make the launcher executable
chmod 550 pdfriend

# Setup PATH
if [ -f "$HOME/.bash_profile" ]; then
	target="$HOME/.bash_profile"
elif [ -f "$HOME/.bashrc" ]; then
	target="$HOME/.bashrc"
else
	target="$HOME/.profile"
fi

cat << 'EOF' >> "$target"
# Add PDFriend installation directory to PATH
export PATH="$PATH:%{INSTALL_PATH}"
EOF
