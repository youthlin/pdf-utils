msgfmt  --java2 -d .  -r com.youthlin.pdf.lang.Message -l zh_CN com/youthlin/pdf/lang/Message_zh_CN.po
# [windows poedit] on /src/main/resources execute:
# "%Poedit_HOME%\GettextTools\bin\msgfmt.exe"  --java2 -d .  -r com.youthlin.pdf.lang.Message -l zh_CN com\youthlin\pdf\lang\Message_zh_CN.po

# msgfmt
# --java2 JDK1.2+ use object[]. or else use hashtable
# -d . output dir
# -r baseName
# -l zh_CN locale
# and last is the .po file
