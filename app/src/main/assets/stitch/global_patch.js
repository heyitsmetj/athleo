const fs = require('fs');
const path = require('path');

function processDirectory(dirPath) {
    const items = fs.readdirSync(dirPath);
    for (const item of items) {
        const fullPath = path.join(dirPath, item);
        const stat = fs.statSync(fullPath);
        if (stat.isDirectory()) {
            processDirectory(fullPath);
        } else if (item === 'code.html' || item.endsWith('.html')) {
            let content = fs.readFileSync(fullPath, 'utf8');
            let modified = false;

            // 1. Add global scrollbar hiding CSS
            if (!content.includes('::-webkit-scrollbar { display: none; }')) {
                // Find </style> or <style>
                if (content.includes('</style>')) {
                    content = content.replace('</style>', '  ::-webkit-scrollbar { display: none; }\n        .no-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }\n    </style>');
                    modified = true;
                }
            }

            // 2. Add dynamic greeting function and call it if greeting-text exists
            if (content.includes('id="greeting-text"')) {
                if (!content.includes('function getGreeting()')) {
                    const greetingScript = `
        function getGreeting() {
            const hour = new Date().getHours();
            if (hour < 12) return "Good Morning";
            if (hour < 17) return "Good Afternoon";
            return "Good Evening";
        }`;
                    // Inject before window.onload or at the end of scripts.  We'll inject into window.renderProfile
                    if (content.includes('window.renderProfile = (user) => {')) {
                       // We can replace the literal greeting setting.
                       const oldGreetingLine = `document.getElementById('greeting-text').innerText = "Namaste, " + user.name.split(' ')[0]`;
                       if (content.includes(oldGreetingLine)) {
                           // Construct the replace carefully.
                           // Actually the exact line varies, let's just make it a regex
                           content = content.replace(/document\.getElementById\('greeting-text'\)\.innerText\s*=\s*"Namaste,[^;]+;/g, 
                           `const h = new Date().getHours(); const g = h<12?"Good Morning":h<17?"Good Afternoon":"Good Evening"; document.getElementById('greeting-text').innerText = g + ", " + user.name.split(' ')[0] + "!";`);
                           modified = true;
                       }
                       const oldGreetingLine2 = `document.getElementById('greeting-text').innerText = "Namaste, " + user.name.split(' ')[0];`;
                       if (content.includes(oldGreetingLine2)) {
                           content = content.replace(oldGreetingLine2, `const h = new Date().getHours(); const g = h<12?"Good Morning":h<17?"Good Afternoon":"Good Evening"; document.getElementById('greeting-text').innerText = g + ", " + user.name.split(' ')[0];`);
                           modified = true;
                       }
                    }
                }
            }

            if (modified) {
                fs.writeFileSync(fullPath, content, 'utf8');
                console.log('Modified:', fullPath);
            }
        }
    }
}

processDirectory(__dirname);
console.log('Done.');
