const fs = require('fs');
const path = require('path');

function processDirectory(dirPath) {
    const items = fs.readdirSync(dirPath);
    for (const item of items) {
        const fullPath = path.join(dirPath, item);
        const stat = fs.statSync(fullPath);
        if (stat.isDirectory()) {
            processDirectory(fullPath);
        } else if (item.endsWith('.html')) {
            let content = fs.readFileSync(fullPath, 'utf8');
            let modified = false;

            // Simple regex to match the <a> tag that contains Android.openProfile() and ends with </a>
            const regex = /<a[^>]+onclick="Android\.openProfile\(\)"[^>]*>[\s\S]*?<\/a>/g;
            if (regex.test(content)) {
                content = content.replace(regex, '');
                // Since we removed 1 item, we might want to change justify-between or min-w-[64px]
                // but usually justify-between will just space the remaining 3 items evenly, which is fine.
                modified = true;
            }

            if (modified) {
                fs.writeFileSync(fullPath, content, 'utf8');
                console.log('Modified:', fullPath);
            }
        }
    }
}

processDirectory(path.join(__dirname, 'app/src/main/assets/stitch'));
console.log('Profile tab removed from bottom navs.');
