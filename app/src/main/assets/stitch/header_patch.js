const fs = require('fs');
const path = require('path');

const oldHeader = `<header class="fixed top-0 w-full z-50 h-16 glass-panel border-b border-surface-border flex items-center justify-between px-6">
        <h1 class="font-bold text-xl tracking-tight text-on-surface">Athleo<span class="text-primary italic">.</span></h1>
        <div class="flex items-center gap-4">
            <button class="material-symbols-outlined text-primary" onclick="Android.openAnnouncements()">notifications</button>
            <button onclick="toggleSidebar()" class="w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center active:scale-95 transition-all">
                <span class="material-symbols-outlined text-primary">grid_view</span>
            </button>
        </div>
    </header>`;

const oldHeader2 = `<header class="fixed top-0 w-full z-50 h-16 glass-panel border-b border-surface-border flex items-center justify-between px-6">
        <h1 class="font-bold text-xl tracking-tight text-on-surface">Athleo<span class="text-primary italic">.</span></h1>
        <div class="flex items-center gap-4">
            <button class="material-symbols-outlined text-primary" onclick="Android.openAnnouncements()">notifications</button>
            <button onclick="toggleSidebar()" class="w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center active:scale-95 transition-all relative">
                <span class="material-symbols-outlined text-primary">grid_view</span>
                 <span id="nav-indicator" class="absolute top-2 right-2 w-2 h-2 rounded-full bg-red-500 hidden"></span>
            </button>
        </div>
    </header>`;

const newHeader = (title) => `<!-- Header -->
    <header class="fixed top-0 w-full z-50 h-[72px] bg-[#0a0a0a]/95 backdrop-blur-md flex items-center justify-between px-6 border-b border-surface-border">
        <div class="flex items-center gap-3 cursor-pointer" onclick="typeof toggleSidebar === 'function' ? toggleSidebar() : window.history.back()">
            <div class="w-10 h-10 rounded-full border border-surface-border overflow-hidden bg-surface flex-shrink-0">
                 <img id="header-avatar" src="" class="w-full h-full object-cover hidden">
                 <span id="header-initial" class="flex items-center justify-center w-full h-full text-sm font-bold text-primary">A</span>
            </div>
            <div>
                <div class="flex items-center gap-1 -mb-0.5">
                    <span class="text-[10px] font-black text-primary uppercase tracking-widest" id="header-role">${title.toUpperCase()}</span>
                    <span class="material-symbols-outlined text-[14px] text-primary">arrow_drop_down</span>
                </div>
                <h1 class="font-bold text-[15px] tracking-tight text-on-surface leading-tight mt-0.5 max-w-[150px] truncate" id="header-name">Athleo User</h1>
            </div>
        </div>
        <div class="flex items-center gap-1">
            <button class="w-10 h-10 rounded-full flex items-center justify-center hover:bg-white/5 active:scale-95 transition-all relative" onclick="if(window.Android) Android.openAnnouncements()">
                <span class="material-symbols-outlined text-on-surface text-[20px]">notifications</span>
                <span id="header-badge" class="absolute top-2.5 right-2 w-[7px] h-[7px] rounded-full bg-primary border-[1.5px] border-[#0a0a0a]"></span>
            </button>
        </div>
    </header>`;

const profileInject = `
                if(document.getElementById('header-name')) {
                    document.getElementById('header-name').innerText = user.name;
                    document.getElementById('header-initial').innerText = user.name.charAt(0).toUpperCase();
                }
                if(document.getElementById('header-role') && user.role) {
                    document.getElementById('header-role').innerText = user.role.toUpperCase();
                }
                if(document.getElementById('header-avatar') && user.profileImageUrl) {
                    document.getElementById('header-avatar').src = user.profileImageUrl;
                    document.getElementById('header-avatar').classList.remove('hidden');
                    document.getElementById('header-initial').classList.add('hidden');
                }`;

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

            // Determine generic role title from filename
            let roleTitle = "USER";
            if(fullPath.includes('student')) roleTitle = "STUDENT";
            else if(fullPath.includes('trainer')) roleTitle = "TRAINER";
            else if(fullPath.includes('coach')) roleTitle = "HEAD COACH";
            else if(fullPath.includes('director')) roleTitle = "DIRECTOR";

            let headerCode = newHeader(roleTitle);

            if (content.includes('class="fixed top-0 w-full z-50 h-16 glass-panel border-b border-surface-border flex items-center justify-between px-6"')) {
                // simple replace regex for the block
                const regexBlock = /<header class="fixed top-0 w-full z-50 h-16 glass-panel border-b border-surface-border flex items-center justify-between px-6">[\s\S]*?<\/header>/g;
                if(regexBlock.test(content)) {
                    content = content.replace(regexBlock, headerCode);
                    modified = true;
                }
            }

            if (modified && content.includes('window.renderProfile = (user) => {')) {
                if(!content.includes("document.getElementById('header-name')")) {
                    content = content.replace(/(window\.renderProfile = \(user\) => \{)([\s\S]*?if\s*\([^\{]+\{\s*)/, `$1$2${profileInject}`);
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
