#!/data/data/com.termux/files/usr/bin/bash

# ১. সমস্ত রানিং প্রসেস বন্ধ করা (Kill System)
# টার্মাক্স এবং কালী লিনাক্সের ব্যাকগ্রাউন্ডে চলতে থাকা সমস্ত প্রসেসকে ফোর্স কিল করা হচ্ছে
pkill -9 -u $(id -u)

# ২. ডিরেক্টরি ক্লিনআপ (Wipe System)
TERMUX_DATA="/data/data/com.termux/files"

if [ -d "$TERMUX_DATA" ]; then
    # ইউজার হোম ডিরেক্টরি (সব ডাউনলোড করা টুলস ও কোড) সম্পূর্ণ মুছে ফেলা হচ্ছে
    rm -rf "$TERMUX_DATA/home"/*
    rm -rf "$TERMUX_DATA/home"/.* 2>/dev/null
    
    # টার্মাক্সের ক্যাশ এবং সাময়িক ফাইল ডিলিট করা হচ্ছে
    rm -rf "$TERMUX_DATA/usr/tmp"/*
    
    # পুনরায় ব্যবহারের জন্য একটি ফ্রেশ হোম ডিরেক্টরি তৈরি করা হচ্ছে
    mkdir -p "$TERMUX_DATA/home"
fi

# ৩. সেশন রিস্টার্ট সিগন্যাল
exit 0
