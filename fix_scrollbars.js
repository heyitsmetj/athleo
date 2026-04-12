const fs = require('fs');
const path = require('path');

function processDirectory(dirPath) {
    const items = fs.readdirSync(dirPath);
    for (const item of items) {
        const fullPath = path.join(dirPath, item);
        const stat = fs.statSync(fullPath);
        if (stat.isDirectory()) {
            processDirectory(fullPath);
        } else if (item.endsWith('.java')) {
            let content = fs.readFileSync(fullPath, 'utf8');
            let modified = false;

            // Look for setupWebView() implementation
            if (content.includes('private void setupWebView() {') && !content.includes('setVerticalScrollBarEnabled')) {
                // inject webView.setVerticalScrollBarEnabled(false) after settings.setJavaScriptEnabled(true);
                // or just after setupWebView() {
                content = content.replace(/private void setupWebView\(\)\s*\{/, 'private void setupWebView() {\n        webView.setVerticalScrollBarEnabled(false);\n        webView.setHorizontalScrollBarEnabled(false);');
                modified = true;
            }

            if (modified) {
                fs.writeFileSync(fullPath, content, 'utf8');
                console.log('Modified:', fullPath);
            }
        }
    }
}

processDirectory(path.join(__dirname, 'app/src/main/java/com/example/athleo'));
console.log('Scrollbars fixed globally.');
