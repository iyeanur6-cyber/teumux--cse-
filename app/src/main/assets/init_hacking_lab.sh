#!/data/data/com.termux/files/usr/bin/bash

# ১. সিস্টেম আপডেট ও কোর প্যাকেজ ইনস্টলেশন
pkg update -y && pkg upgrade -y
pkg install -y tor obfs4proxy proot-distro git python nmap

# ২. Tor এবং Obfs4 টানেলিং কনফিগারেশন
TORRC_PATH="/data/data/com.termux/files/usr/etc/tor/torrc"

if [ -f "$TORRC_PATH" ]; then
    # Torrc ফাইলে SOCKS5 প্রক্সি পোর্ট এবং Obfs4 ব্রিজ পাথ সেটআপ
    echo "ClientTransportPlugin obfs4 exec /data/data/com.termux/files/usr/bin/obfs4proxy" >> $TORRC_PATH
    echo "SocksPort 127.0.0.1:9050" >> $TORRC_PATH
    echo "UseBridges 1" >> $TORRC_PATH
fi

# ৩. PRoot-এর মাধ্যমে Kali Linux ল্যাব সেটআপ
echo "[YI-LAB] Installing Kali Linux Nethunter (PRoot)..."
proot-distro install kali

# ৪. শর্টকাট কমান্ড তৈরি (যাতে সহজে লগইন করা যায়)
echo "alias start-kali='proot-distro login kali'" >> ~/.bashrc
echo "alias start-tor='tor -f $TORRC_PATH &'" >> ~/.bashrc

echo "[YI-LAB] Setup Completed Successfully!"
