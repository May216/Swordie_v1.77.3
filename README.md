# Swordie v1.77.3

不包含 client、dat，需先安裝 JDK 17。

## 若已有安裝別的版本 JDK 怎麼辦 ?

可以分別新增環境變數，比如：`JAVA16_HOME`, `JAVA17_HOME`

然後新增一個 `JAVA_HOME` 值為 `%JAVA17_HOME%\bin;%JAVA17_HOME%\jre\bin;`，需要換回 16 就改成 %JAVA16_HOME%

確認 JDK 版本: `java --version`

## 啟動方式

1. 將 dat 丟入到 server folder
2. 使用 IDEA IDE 開啟 swordie server folder
3. file -> project structure -> SDK 選擇 JDK 17, language level 選 `16 - Records, patterns...`
4. project settings -> modules -> 選中 dat 及 bin 後按上方工具列的 excluded
5. 將所有 sql 檔導入 swordie db 中
6. 開啟 moonlight.sln, 在專案中加入參考 -> `MySql.Data` (v8.0.29.0)
7. Client.cs 檢查 MySqlConnection 資訊是否正確 `server=localhost;port=3306;username=root;password=;database=swordie`
8. 選 release 後建置 moonlight 專案, 產生 moonlight.exe
9. 把 `moonlight.exe` 丟入 client fodler 中後以管理員身分執行按 play 啟動遊戲

## 修改項目

### 0710
1. 支援腳本中文化: 在 string 前面加上 u 即可
2. moonlight 編譯