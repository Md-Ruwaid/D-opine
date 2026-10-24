// StrictLock (Regain Edition) — Interactive Website Logic

document.addEventListener('DOMContentLoaded', () => {

    // 1. Phone Mockup Screen Tab Switcher
    const tabs = document.querySelectorAll('.preview-tab');
    const screenViews = document.querySelectorAll('.screen-view');

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const screenId = tab.getAttribute('data-screen');

            // Set active tab
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');

            // Set active view
            screenViews.forEach(view => {
                view.classList.remove('active');
                if (view.id === `view-${screenId}`) {
                    view.classList.add('active');
                }
            });
        });
    });

    // 2. Interactive Mindful Breathing Simulator Widget
    const breathingCircle = document.getElementById('breathing-circle');
    const phaseText = document.getElementById('breathing-phase');
    const countText = document.getElementById('breathing-count');
    const toggleBtn = document.getElementById('btn-toggle-breathing');

    let isBreathingActive = false;
    let breathInterval = null;
    let timerInterval = null;
    let secondsLeft = 4;
    let currentPhase = 'Inhale'; // 'Inhale', 'Hold', 'Exhale'

    function startBreathingCycle() {
        isBreathingActive = true;
        toggleBtn.textContent = 'Pause Mindful Session';
        toggleBtn.classList.add('btn-primary');
        toggleBtn.classList.remove('btn-outline');

        runPhase('Inhale', 4, 1.25);
    }

    function stopBreathingCycle() {
        isBreathingActive = false;
        clearInterval(breathInterval);
        clearInterval(timerInterval);
        toggleBtn.textContent = 'Start Mindful Session';
        toggleBtn.classList.remove('btn-primary');
        toggleBtn.classList.add('btn-outline');

        phaseText.textContent = 'Inhale';
        countText.textContent = '4';
        breathingCircle.style.transform = 'scale(1)';
    }

    function runPhase(phase, duration, scale) {
        if (!isBreathingActive) return;

        currentPhase = phase;
        phaseText.textContent = phase;
        secondsLeft = duration;
        countText.textContent = secondsLeft;
        breathingCircle.style.transform = `scale(${scale})`;

        clearInterval(timerInterval);
        timerInterval = setInterval(() => {
            secondsLeft -= 1;
            if (secondsLeft >= 0) {
                countText.textContent = secondsLeft;
            }
        }, 1000);

        clearInterval(breathInterval);
        breathInterval = setTimeout(() => {
            if (phase === 'Inhale') {
                runPhase('Hold', 3, 1.25);
            } else if (phase === 'Hold') {
                runPhase('Exhale', 4, 0.85);
            } else {
                runPhase('Inhale', 4, 1.25);
            }
        }, duration * 1000);
    }

    toggleBtn.addEventListener('click', () => {
        if (isBreathingActive) {
            stopBreathingCycle();
        } else {
            startBreathingCycle();
        }
    });

    // 3. QR Code Generator & Modal
    const qrModal = document.getElementById('qr-modal');
    const openQrBtn = document.getElementById('btn-qr-modal');
    const closeQrBtn = document.getElementById('btn-close-modal');
    const qrContainer = document.getElementById('qr-placeholder');
    const networkUrlText = document.getElementById('network-url-text');

    // Automatically resolve network IP for phone QR scanning so phones don't try to load localhost
    const isLocalhost = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
    const phoneBaseUrl = isLocalhost 
        ? 'http://10.146.8.144:3000' 
        : window.location.origin;
    const downloadUrl = `${phoneBaseUrl}/StrictLock-Regain-v1.0.1.apk`;
    networkUrlText.textContent = downloadUrl;

    openQrBtn.addEventListener('click', () => {
        renderQRCode(downloadUrl);
        qrModal.classList.add('open');
    });

    closeQrBtn.addEventListener('click', () => {
        qrModal.classList.remove('open');
    });

    // Copy network download link
    const copyBtn = document.getElementById('btn-copy-url');
    const copyText = document.getElementById('copy-text');
    if (copyBtn) {
        copyBtn.addEventListener('click', () => {
            navigator.clipboard.writeText(downloadUrl).then(() => {
                copyText.textContent = 'Copied!';
                setTimeout(() => {
                    copyText.textContent = 'Copy';
                }, 2000);
            }).catch(() => {
                copyText.textContent = 'Failed';
            });
        });
    }

    qrModal.addEventListener('click', (e) => {
        if (e.target === qrModal) {
            qrModal.classList.remove('open');
        }
    });

    // SVG QR Code Renderer (Lightweight QR Generator using Google Chart or QR API)
    function renderQRCode(url) {
        qrContainer.innerHTML = '';
        const img = document.createElement('img');
        img.alt = 'StrictLock APK QR Code';
        img.style.width = '100%';
        img.style.height = '100%';
        img.style.objectFit = 'contain';
        img.src = `https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=${encodeURIComponent(url)}&bgcolor=ffffff&color=051A0D`;
        qrContainer.appendChild(img);
    }
});
