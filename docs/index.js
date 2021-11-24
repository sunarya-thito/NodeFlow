const MEDIA = [{"type":"youtube", "media":"ckwZN9jMQ5k"}];
const downloadURL = 'https://github.com/sunarya-thito/NodeFlowDistribution/archive/refs/heads/master.zip';
document.addEventListener('contextmenu', event => event.preventDefault());
function download(url) {
    document.getElementById('dummy').src = url;
}
function openInNewTab(url) {
    window.open(url, '_blank').focus();
}
$(document).ready(() => {
    //
    let scrollable = new SimpleBar($('#container')[0]);
    let scrollElement = scrollable.getScrollElement();
    $('#online-guides-button').click(() => {
        openInNewTab('https://nodeflow.gitbook.io/nodeflow/');
    });
    $('#report-button').click(() => {
        openInNewTab('https://github.com/sunarya-thito/NodeFlow/issues');
    });
    $('#donate-button').click(() => {
        openInNewTab('https://paypal.me/sunaryathito');
    });
    $('#join-discord-button').click(() => {
        openInNewTab('https://discord.gg/DquZxC4ZeB');
    });
    $('#download-java').click(() => {
        openInNewTab('https://adoptium.net/archive.html?variant=openjdk16');
    });
    $(scrollElement).scroll(() => {
        if ($(scrollElement).scrollTop() > $('#intro').height() - $('#header').height()) {
            $('.full-header-bg').removeClass('transparent-header');
        } else if ($(scrollElement).scrollTop() > 0) {
            $('.full-header-bg').addClass('transparent-header');
            $('.shady-header-bg').removeClass('transparent-header');
        } else {
            $('.full-header-bg').addClass('transparent-header');
            $('.shady-header-bg').addClass('transparent-header');
        }
    });
    let children = $('#slide-container').children();
    children.each((index, element) => {
        if (index === 0) {
            $('#slider').append('<li data-target="big-header" data-slide-to="'+index+'" class="active"></li>');
            $(element).addClass('active');
        } else {
            $('#slider').append('<li data-target="big-header" data-slide-to="'+index+'"></li>');
        }
        $(element).addClass('carousel-item');
    });
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
    
            document.querySelector(this.getAttribute('href')).scrollIntoView({
                behavior: 'smooth'
            });
        });
    });
    fetch('https://raw.githubusercontent.com/sunarya-thito/NodeFlow/master/Resources/src/main/resources/ChangeLogs.txt')
    .then(result => result.text())
    .then(text => {
        $('#changelogs').html(marked.parse(parseVersionHeader(text)));
    });
    checkOS();
    MEDIA.forEach(m => {
        loadMedia(m);
    });
});

function checkOS() {
    if (jscd.os == 'Windows') {
        $('#download-button').click(() => {
            download(downloadURL);
        });
        $('#download-button > .inner-button').html('Download for '+jscd.os + ' ' + jscd.osVersion);
        $('#download-button').removeClass('disabled-button');
        $('#force-download').remove();
    } else {
        $('#download-button > .inner-button').html('Does not support '+jscd.os);
        $('#download-button').addClass('disabled-button');
        $('#force-download').attr('href', downloadURL);
    }
}

function loadMedia(media) {
    if (media.type == 'youtube') {
        let element = document.createElement('div');
        let frame = document.createElement('iframe');
        frame.setAttribute('frameborder', '0');
        frame.setAttribute('width', '800');
        frame.setAttribute('height', '600');
        frame.setAttribute('allow', 'encrypted-media; gyroscope; picture-in-picture');
        frame.setAttribute('src', 'https://www.youtube.com/embed/'+media.media);
        element.appendChild(frame);
        document.getElementById('media-container').appendChild(element);
    } else if (media.type == 'image') {

    }
}

function parseVersionHeader(text) {
    let split = text.split('\n');
    for (let i = 0; i < split.length; i++) {
        if (split[i].startsWith('[') && split[i].endsWith(']')) {
            split[i] = '### Version ' + split[i].substring(1, split[i].length - 1);
        }
    }
    return split.join('\n');
}

(function (window) {
    {
        var unknown = '-';

        // screen
        var screenSize = '';
        if (screen.width) {
            width = (screen.width) ? screen.width : '?';
            height = (screen.height) ? screen.height : '?';
            screenSize += '' + width + " x " + height;
        }

        // browser
        var nVer = navigator.appVersion;
        var nAgt = navigator.userAgent;
        var browser = navigator.appName;
        var version = '' + parseFloat(navigator.appVersion);
        var majorVersion = parseInt(navigator.appVersion, 10);
        var nameOffset, verOffset, ix;

        // Opera
        if ((verOffset = nAgt.indexOf('Opera')) != -1) {
            browser = 'Opera';
            version = nAgt.substring(verOffset + 6);
            if ((verOffset = nAgt.indexOf('Version')) != -1) {
                version = nAgt.substring(verOffset + 8);
            }
        }
        // Opera Next
        if ((verOffset = nAgt.indexOf('OPR')) != -1) {
            browser = 'Opera';
            version = nAgt.substring(verOffset + 4);
        }
        // Legacy Edge
        else if ((verOffset = nAgt.indexOf('Edge')) != -1) {
            browser = 'Microsoft Legacy Edge';
            version = nAgt.substring(verOffset + 5);
        } 
        // Edge (Chromium)
        else if ((verOffset = nAgt.indexOf('Edg')) != -1) {
            browser = 'Microsoft Edge';
            version = nAgt.substring(verOffset + 4);
        }
        // MSIE
        else if ((verOffset = nAgt.indexOf('MSIE')) != -1) {
            browser = 'Microsoft Internet Explorer';
            version = nAgt.substring(verOffset + 5);
        }
        // Chrome
        else if ((verOffset = nAgt.indexOf('Chrome')) != -1) {
            browser = 'Chrome';
            version = nAgt.substring(verOffset + 7);
        }
        // Safari
        else if ((verOffset = nAgt.indexOf('Safari')) != -1) {
            browser = 'Safari';
            version = nAgt.substring(verOffset + 7);
            if ((verOffset = nAgt.indexOf('Version')) != -1) {
                version = nAgt.substring(verOffset + 8);
            }
        }
        // Firefox
        else if ((verOffset = nAgt.indexOf('Firefox')) != -1) {
            browser = 'Firefox';
            version = nAgt.substring(verOffset + 8);
        }
        // MSIE 11+
        else if (nAgt.indexOf('Trident/') != -1) {
            browser = 'Microsoft Internet Explorer';
            version = nAgt.substring(nAgt.indexOf('rv:') + 3);
        }
        // Other browsers
        else if ((nameOffset = nAgt.lastIndexOf(' ') + 1) < (verOffset = nAgt.lastIndexOf('/'))) {
            browser = nAgt.substring(nameOffset, verOffset);
            version = nAgt.substring(verOffset + 1);
            if (browser.toLowerCase() == browser.toUpperCase()) {
                browser = navigator.appName;
            }
        }
        // trim the version string
        if ((ix = version.indexOf(';')) != -1) version = version.substring(0, ix);
        if ((ix = version.indexOf(' ')) != -1) version = version.substring(0, ix);
        if ((ix = version.indexOf(')')) != -1) version = version.substring(0, ix);

        majorVersion = parseInt('' + version, 10);
        if (isNaN(majorVersion)) {
            version = '' + parseFloat(navigator.appVersion);
            majorVersion = parseInt(navigator.appVersion, 10);
        }

        // mobile version
        var mobile = /Mobile|mini|Fennec|Android|iP(ad|od|hone)/.test(nVer);

        // cookie
        var cookieEnabled = (navigator.cookieEnabled) ? true : false;

        if (typeof navigator.cookieEnabled == 'undefined' && !cookieEnabled) {
            document.cookie = 'testcookie';
            cookieEnabled = (document.cookie.indexOf('testcookie') != -1) ? true : false;
        }

        // system
        var os = unknown;
        var clientStrings = [
            {s:'Windows 10', r:/(Windows 10.0|Windows NT 10.0)/},
            {s:'Windows 8.1', r:/(Windows 8.1|Windows NT 6.3)/},
            {s:'Windows 8', r:/(Windows 8|Windows NT 6.2)/},
            {s:'Windows 7', r:/(Windows 7|Windows NT 6.1)/},
            {s:'Windows Vista', r:/Windows NT 6.0/},
            {s:'Windows Server 2003', r:/Windows NT 5.2/},
            {s:'Windows XP', r:/(Windows NT 5.1|Windows XP)/},
            {s:'Windows 2000', r:/(Windows NT 5.0|Windows 2000)/},
            {s:'Windows ME', r:/(Win 9x 4.90|Windows ME)/},
            {s:'Windows 98', r:/(Windows 98|Win98)/},
            {s:'Windows 95', r:/(Windows 95|Win95|Windows_95)/},
            {s:'Windows NT 4.0', r:/(Windows NT 4.0|WinNT4.0|WinNT|Windows NT)/},
            {s:'Windows CE', r:/Windows CE/},
            {s:'Windows 3.11', r:/Win16/},
            {s:'Android', r:/Android/},
            {s:'Open BSD', r:/OpenBSD/},
            {s:'Sun OS', r:/SunOS/},
            {s:'Chrome OS', r:/CrOS/},
            {s:'Linux', r:/(Linux|X11(?!.*CrOS))/},
            {s:'iOS', r:/(iPhone|iPad|iPod)/},
            {s:'Mac OS X', r:/Mac OS X/},
            {s:'Mac OS', r:/(Mac OS|MacPPC|MacIntel|Mac_PowerPC|Macintosh)/},
            {s:'QNX', r:/QNX/},
            {s:'UNIX', r:/UNIX/},
            {s:'BeOS', r:/BeOS/},
            {s:'OS/2', r:/OS\/2/},
            {s:'Search Bot', r:/(nuhk|Googlebot|Yammybot|Openbot|Slurp|MSNBot|Ask Jeeves\/Teoma|ia_archiver)/}
        ];
        for (var id in clientStrings) {
            var cs = clientStrings[id];
            if (cs.r.test(nAgt)) {
                os = cs.s;
                break;
            }
        }

        var osVersion = unknown;

        if (/Windows/.test(os)) {
            osVersion = /Windows (.*)/.exec(os)[1];
            os = 'Windows';
        }

        switch (os) {
            case 'Mac OS':
            case 'Mac OS X':
            case 'Android':
                osVersion = /(?:Android|Mac OS|Mac OS X|MacPPC|MacIntel|Mac_PowerPC|Macintosh) ([\.\_\d]+)/.exec(nAgt)[1];
                break;

            case 'iOS':
                osVersion = /OS (\d+)_(\d+)_?(\d+)?/.exec(nVer);
                osVersion = osVersion[1] + '.' + osVersion[2] + '.' + (osVersion[3] | 0);
                break;
        }
        
        // flash (you'll need to include swfobject)
        /* script src="//ajax.googleapis.com/ajax/libs/swfobject/2.2/swfobject.js" */
        var flashVersion = 'no check';
        if (typeof swfobject != 'undefined') {
            var fv = swfobject.getFlashPlayerVersion();
            if (fv.major > 0) {
                flashVersion = fv.major + '.' + fv.minor + ' r' + fv.release;
            }
            else  {
                flashVersion = unknown;
            }
        }
    }

    window.jscd = {
        screen: screenSize,
        browser: browser,
        browserVersion: version,
        browserMajorVersion: majorVersion,
        mobile: mobile,
        os: os,
        osVersion: osVersion,
        cookies: cookieEnabled,
        flashVersion: flashVersion
    };
}(this));