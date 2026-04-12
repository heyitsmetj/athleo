const fs = require('fs');
const path = require('path');

const mockupsDir = path.join(__dirname, 'Actual_UI_Mockups');
if (!fs.existsSync(mockupsDir)) {
    fs.mkdirSync(mockupsDir);
}

const sourceDir = path.join(__dirname, 'app/src/main/assets/stitch');

const screensToMock = [
    {
        folder: 'login_screen_dark',
        replacements: [
            { rx: /(<input[^>]*id="email"[^>]*)>/gi, rep: '$1 value="TD_Test@athleo.com">' },
            { rx: /(<input[^>]*id="password"[^>]*)>/gi, rep: '$1 value="admin123!">' }
        ]
    },
    {
        folder: 'director_dashboard_1',
        replacements: [
            { rx: /id="header-name"[^>]*>.*?<\/h1>/gi, rep: 'id="header-name">John Doe</h1>' },
            { rx: /id="header-role"[^>]*>.*?<\/span>/gi, rep: 'id="header-role">TECHNICAL DIRECTOR</span>' },
            { rx: /id="header-initial"[^>]*>.*?<\/span>/gi, rep: 'id="header-initial">J</span>' }
        ]
    },
    {
        folder: 'head_coach_dashboard_dark_2',
        replacements: [
            { rx: /id="header-name"[^>]*>.*?<\/h1>/gi, rep: 'id="header-name">Coach Mike</h1>' },
            { rx: /id="header-role"[^>]*>.*?<\/span>/gi, rep: 'id="header-role">HEAD COACH</span>' },
            { rx: /id="header-initial"[^>]*>.*?<\/span>/gi, rep: 'id="header-initial">M</span>' }
        ]
    },
    {
        folder: 'student_dashboard_dark_3',
        replacements: [
            { rx: /id="header-name"[^>]*>.*?<\/h1>/gi, rep: 'id="header-name">Alex Smith</h1>' },
            { rx: /id="header-role"[^>]*>.*?<\/span>/gi, rep: 'id="header-role">STUDENT & ATHLETE</span>' },
            { rx: /id="header-initial"[^>]*>.*?<\/span>/gi, rep: 'id="header-initial">A</span>' },
            { rx: /<h1>.*?<\/h1>/i, rep: '<h1>Alex Smith</h1>' }
        ]
    },
    {
        folder: 'edit_profile_dark',
        replacements: [
            { rx: /(<input[^>]*id="displayNameInput"[^>]*)>/gi, rep: '$1 value="John Doe">' },
            { rx: /(<textarea[^>]*id="bioInput"[^>]*>)/gi, rep: '$1Passionate Head Coach with 10 years of experience.' },
            { rx: /(<input[^>]*id="roleInput"[^>]*)>/gi, rep: '$1 value="Head Coach">' }
        ]
    },
    {
        folder: 'community_chat_list_dark',
        replacements: [
            { rx: /id="header-name"[^>]*>.*?<\/h1>/gi, rep: 'id="header-name">Messages</h1>' },
            { rx: /id="header-role"[^>]*>.*?<\/span>/gi, rep: 'id="header-role">COMMUNICATIONS</span>' },
            { rx: /id="header-initial"[^>]*>.*?<\/span>/gi, rep: 'id="header-initial"><span class="material-symbols-outlined text-[16px]">chat</span></span>' }
        ]
    },
    {
        folder: 'new_message_dark',
        replacements: [
            { rx: /(<input[^>]*id="messageTo"[^>]*)>/gi, rep: '$1 value="All Students Group">' },
            { rx: /(<textarea[^>]*id="messageBody"[^>]*>)/gi, rep: '$1Please remember to bring your extra gear tomorrow for the special session.' }
        ]
    },
    {
        folder: 'create_task_dark',
        replacements: [
            { rx: /(<input[^>]*id="taskTitle"[^>]*)>/gi, rep: '$1 value="Daily Dribbling Drills">' },
            { rx: /(<textarea[^>]*id="taskDescription"[^>]*>)/gi, rep: '$1Complete 50 repetitions of figure-8 dribbles. Record your best attempt.' },
            { rx: /(<input[^>]*id="taskDeadline"[^>]*)>/gi, rep: '$1 value="2026-04-01">' },
            { rx: /id="header-name"[^>]*>.*?<\/h1>/gi, rep: 'id="header-name">Create Task</h1>' },
            { rx: /id="header-role"[^>]*>.*?<\/span>/gi, rep: 'id="header-role">ASSIGNMENTS</span>' },
            { rx: /id="header-initial"[^>]*>.*?<\/span>/gi, rep: 'id="header-initial"><span class="material-symbols-outlined text-[16px]">assignment</span></span>' }
        ]
    },
    {
        folder: 'task_management_hub_dark',
        replacements: [
            { rx: /id="header-name"[^>]*>.*?<\/h1>/gi, rep: 'id="header-name">Tasks Hub</h1>' }
        ]
    },
    {
        folder: 'roster_analytics_dark',
        replacements: [
            { rx: /id="header-name"[^>]*>.*?<\/h1>/gi, rep: 'id="header-name">Team Roster Analytics</h1>' },
            { rx: /id="header-role"[^>]*>.*?<\/span>/gi, rep: 'id="header-role">PERFORMANCE</span>' }
        ]
    },
    {
        folder: 'attendance_tracker_dark',
        replacements: [
            { rx: /id="header-name"[^>]*>.*?<\/h1>/gi, rep: 'id="header-name">Attendance</h1>' }
        ]
    },
    {
        folder: 'financial_overview_dark_2',
        replacements: [
            { rx: /id="header-name"[^>]*>.*?<\/h1>/gi, rep: 'id="header-name">Expenses & Finances</h1>' },
            { rx: /id="header-role"[^>]*>.*?<\/span>/gi, rep: 'id="header-role">ADMINISTRATION</span>' }
        ]
    }
];

function generateMockups() {
    let indexHtml = `
    <html>
    <head>
    <title>Actual UI Mockups</title>
    <style>
        body { background:#0a0a0a; color:#eee; font-family:sans-serif; padding: 2rem; margin: 0; }
        .gallery { display:flex; gap: 3rem; flex-wrap: wrap; justify-content: center; }
        .card { 
            display: flex; flex-direction: column; align-items: center; 
            background: #111; border: 1px solid #333; padding: 20px; border-radius: 20px;
        }
        .frame { 
            border: 12px solid #262626; border-radius: 40px; overflow: hidden; background: #000; 
            position: relative; width: 375px; height: 812px; margin-top: 10px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.8);
        }
        h1 { text-align: center; margin-bottom: 2rem; color: #6bff8f; font-size: 2.5rem; }
        h2 { text-align: center; font-size: 1.2rem; color: #fff; margin: 0; letter-spacing: 1px; }
        p.subtitle { text-align: center; color: #888; font-size: 1rem; margin-top: -10px; margin-bottom: 30px; }
    </style>
    </head>
    <body>
    <h1>Athleo Android UI Portfolio</h1>
    <p class="subtitle">A complete collection of exact rendered screens directly from the app's codebase.</p>
    <div class="gallery">
    `;

    for (let screen of screensToMock) {
        let screenPath = path.join(sourceDir, screen.folder, 'code.html');
        if (fs.existsSync(screenPath)) {
            let content = fs.readFileSync(screenPath, 'utf8');
            
            // Apply replacements
            for(let rep of screen.replacements) {
                content = content.replace(rep.rx, rep.rep);
            }
            
            // Inject a mock Android object so the existing scripts don't crash and remove dynamic header_name replacements from JS
            const mockAndroidScript = `
            <script>
                window.Android = {
                    login: function() { console.log("Mock Login"); },
                    registerUser: function() { console.log("Mock Register"); },
                    openAnnouncements: function() { console.log("Mock Announcements"); },
                    seedTestAccounts: function() { console.log("Mock Seed"); },
                    saveProfile: function() { console.log("Mock Save Profile"); },
                    closeScreen: function() { console.log("Mock Close"); },
                    goBack: function() { console.log("Mock Back"); },
                    createTask: function() { console.log("Mock Task"); },
                    sendMessage: function() { console.log("Mock msg"); },
                    showToast: function() { console.log("Toast shown"); }
                };
                
                // Block the renderProfile from constantly resetting our mock title
                window.renderProfile = function(user) { console.log("Mock profile render blocked for static HTML"); };
            </script>
            `;
            
            content = content.replace('</head>', mockAndroidScript + '</head>');

            // Remove references to external patches that would 404. These are node scripts that ran anyway
            content = content.replace(/<script src="[^"]*header_patch\.js"><\/script>/gi, '');
            content = content.replace(/<script src="[^"]*global_patch\.js"><\/script>/gi, '');

            let outPath = path.join(mockupsDir, screen.folder + '.html');
            fs.writeFileSync(outPath, content, 'utf8');

            let humanName = screen.folder
                .replace(/_dark/g, '')
                .replace(/_[0-9]/g, '')
                .split('_')
                .map(w => w.charAt(0).toUpperCase() + w.slice(1))
                .join(' ');

            indexHtml += `
            <div class="card">
                <h2>${humanName}</h2>
                <div class="frame">
                    <iframe src="${screen.folder}.html" width="100%" height="100%" style="border:none;"></iframe>
                </div>
            </div>`;
            console.log("Mocked", screen.folder);
        } else {
            console.log("Missing", screenPath);
        }
    }

    indexHtml += '</div></body></html>';
    fs.writeFileSync(path.join(mockupsDir, 'index.html'), indexHtml, 'utf8');
    console.log("Mockups generated at", mockupsDir);
}

generateMockups();
