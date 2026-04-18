/**
 * Athleo UI Series - Reusable Top Bar Component
 * Inspired by Zomato, WhatsApp, and Swiggy for a premium hybrid experience.
 */

window.AthleoUI = {
    config: {
        title: "Athleo",
        type: "dashboard", // "dashboard" or "page"
        showNotifications: true,
        onBack: null
    },

    initTopBar: function(config) {
        this.config = { ...this.config, ...config };
        
        // Ensure container exists
        let container = document.getElementById('top-bar-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'top-bar-container';
            document.body.prepend(container);
        }

        this.render();
        
        // Always try to load user profile to fill in details if in dashboard mode
        if (this.config.type === 'dashboard' && window.Android && window.Android.loadUserProfile) {
            window.Android.loadUserProfile();
        }
    },

    render: function() {
        const container = document.getElementById('top-bar-container');
        const isDashboard = this.config.type === 'dashboard';
        
        let actionsHtml = '';
        if (this.config.actions) {
            this.config.actions.forEach(action => {
                actionsHtml += `
                    <button onclick="${action.onclick}" class="w-10 h-10 rounded-full flex items-center justify-center hover:bg-white/5 active:scale-95 transition-all relative">
                        <span class="material-symbols-outlined text-on-surface text-[22px]">${action.icon}</span>
                        ${action.badge ? `<span class="absolute top-2.5 right-2 w-[7px] h-[7px] rounded-full bg-primary border-[1.5px] border-[#0a0a0a]"></span>` : ''}
                    </button>
                `;
            });
        } else if (isDashboard && this.config.showNotifications) {
            actionsHtml = `
                <button onclick="if(window.Android) Android.openAnnouncements()" class="w-10 h-10 rounded-full flex items-center justify-center hover:bg-white/5 active:scale-95 transition-all relative">
                    <span class="material-symbols-outlined text-on-surface text-[22px]">notifications</span>
                    <span id="header-badge" class="absolute top-2.5 right-2 w-[7px] h-[7px] rounded-full bg-primary border-[1.5px] border-[#0a0a0a] hidden"></span>
                </button>
            `;
        }

        const leftContent = isDashboard ? `
            <div class="flex items-center gap-3 cursor-pointer" onclick="if(typeof toggleSidebar === 'function') toggleSidebar(); else if(window.Android) Android.openProfile();">
                <div class="w-10 h-10 rounded-full border border-surface-border overflow-hidden bg-surface flex-shrink-0">
                     <img id="tb-avatar" src="" class="w-full h-full object-cover hidden">
                     <span id="tb-initial" class="flex items-center justify-center w-full h-full text-sm font-bold text-primary">A</span>
                </div>
                <div class="flex flex-col">
                    <div class="flex items-center gap-1 -mb-0.5">
                        <span class="text-[9px] font-black text-primary uppercase tracking-widest" id="tb-role">USER</span>
                        <span class="material-symbols-outlined text-[14px] text-primary">arrow_drop_down</span>
                    </div>
                    <h1 class="font-bold text-[14px] tracking-tight text-on-surface leading-tight mt-0.5 max-w-[140px] truncate" id="tb-name">${this.config.title}</h1>
                </div>
            </div>
        ` : `
            <div class="flex items-center gap-4">
                <button onclick="${this.config.onBack || 'window.history.back()'}" class="w-10 h-10 rounded-full flex items-center justify-center hover:bg-white/5 active:scale-90 transition-all">
                    <span class="material-symbols-outlined text-on-surface-variant">arrow_back</span>
                </button>
                <h1 class="font-bold text-lg tracking-tight text-on-surface">${this.config.title}</h1>
            </div>
        `;

        container.innerHTML = `
            <header class="fixed top-0 w-full z-50 h-[72px] bg-[#0a0a0a]/80 backdrop-blur-xl flex items-center justify-between px-6 border-b border-surface-border transition-all duration-300">
                ${leftContent}
                <div class="flex items-center gap-1">
                    ${actionsHtml}
                </div>
            </header>
            <div class="h-[72px]"></div> <!-- Spacer -->
        `;
    },

    updateProfile: function(user) {
        if (this.config.type !== 'dashboard') return;
        
        const initial = document.getElementById('tb-initial');
        const avatar = document.getElementById('tb-avatar');
        const name = document.getElementById('tb-name');
        const role = document.getElementById('tb-role');

        if (name) name.innerText = user.name || "Athleo User";
        if (role) role.innerText = user.role || "USER";
        
        if (user.profileImageUrl && avatar) {
            avatar.src = user.profileImageUrl;
            avatar.classList.remove('hidden');
            if (initial) initial.classList.add('hidden');
        } else if (initial) {
            initial.innerText = (user.name || "A").charAt(0).toUpperCase();
            initial.classList.remove('hidden');
            if (avatar) avatar.classList.add('hidden');
        }
    }
};

// Global hook for the Android Bridge
window.renderProfile = (user) => {
    if (window.AthleoUI) window.AthleoUI.updateProfile(user);
    // Backward compatibility for existing sidebars
    const sideName = document.getElementById('sidebar-user-name') || document.getElementById('header-name');
    const sideRole = document.getElementById('sidebar-user-role') || document.getElementById('header-role');
    const sideInitial = document.getElementById('sidebar-profile-initial') || document.getElementById('header-initial');
    const sideAvatar = document.getElementById('sidebar-profile-img') || document.getElementById('header-avatar');
    
    if (sideName) sideName.innerText = user.name;
    if (sideRole) sideRole.innerText = user.role;
    if (user.profileImageUrl) {
        if (sideAvatar) { sideAvatar.src = user.profileImageUrl; sideAvatar.classList.remove('hidden'); }
        if (sideInitial) sideInitial.classList.add('hidden');
    } else {
        if (sideInitial) { sideInitial.innerText = user.name.charAt(0).toUpperCase(); sideInitial.classList.remove('hidden'); }
        if (sideAvatar) sideAvatar.classList.add('hidden');
    }
};
