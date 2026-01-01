// Chrome 36.0.0.0 down version check (36.0.0.0 is false)
var oldWeb = /Chrome\/([0-9]|[0-2][0-9]|3[0-5])\.[0-9]+\.[0-9]+\.[0-9]+/.test(window.navigator.userAgent);

// Scroll Move
function scrollMove(t, h, m) {
  'use strict';
  if (h == undefined) h = 0;
  if (m != undefined && jQuery(window).width() < 993) h = m;
  var o = jQuery('html, body');
  if (navigator.userAgent.toLowerCase().match(/trident/i)) {
    o = jQuery('html');
  }
  o.animate(
    {
      scrollTop: jQuery(t).offset().top - h,
    },
    500
  );
}
// Menu Open
function menuOpen(o) {
  'use strict';
  var o = $(o).attr('id'),
    a = -$(window).scrollTop();
  $('#wrap').css('top', a);
  $('body').addClass('gnb-open');
  $('body').addClass(o + '-open');
  if (oldWeb) {
    $('#' + o).show();
  }
}

// Menu Close
function menuClose(o) {
  'use strict';
  var o = $(o).attr('id'),
    originScroll = -$('#wrap').position().top;
  $('body').removeClass(o + '-open');
  $('body').removeClass('gnb-open');
  if (originScroll != -0) {
    $(window).scrollTop(originScroll);
  }
  $('#wrap').removeAttr('style');
  if (oldWeb) {
    $('#' + o).hide();
  }
}

function comma(str) {
  str = String(str);
  return str.replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,');
}
function uncomma(str) {
  str = String(str);
  return str.replace(/[^\d]+/g, '');
}
function inputNumberFormat(obj) {
  obj.value = comma(uncomma(obj.value));
}
jQuery(function ($) {
  var $body = $('body'),
    $window = $(window);

  if (oldWeb) {
    menuClose(gnb);
  }
  $('.js-mn').click(function () {
    if ($body.hasClass('gnb-open')) {
      menuClose(gnb);
    } else {
      menuOpen(gnb);
    }
  });

  $('#ct .animated').addClass('ani-stop');
  function scrollSection() {
    var sT = $window.scrollTop();
    var wH = $window.height();
    $('#wrap')
      .find('.px-motion')
      .each(function () {
        var t = $(this);
        var tT = t.offset().top;
        var tH = t.innerHeight();
        var tD = 90;

        if (t.attr('data-delay')) {
          tD = t.attr('data-delay');
        }
        if (tT - wH < sT - tD && tT + tH > sT) {
          t.find('.animated').removeClass('ani-stop');
          if (t.find('video').length) {
            t.find('video')[0].play();
          }
        } else {
          t.find('.animated').addClass('ani-stop');
          if (t.find('video').length) {
            t.find('video')[0].pause();
            t.find('video')[0].currentTime = 0;
          }
        }
      });
  }

  var lastScrollTop = 0;
  $window
    .scroll(function () {
      var sT = $(this).scrollTop(),
        lnbPosY;

      if ($(this).scrollTop() > 0) {
        $body.addClass('is-scroll');
      } else {
        $body.removeClass('is-scroll');
      }
      if ($('.px-motion').length) {
        scrollSection();
      }

      // up&down
      if (sT > lastScrollTop) {
        $body.addClass('scroll-down');
        $body.removeClass('scroll-up');
      } else {
        $body.removeClass('scroll-down');
        $body.addClass('scroll-up');
      }
      lastScrollTop = sT;
    })
    .trigger('scroll');
});

function getCookie(name) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(';').shift();
}

const goLogin = () => {
  window.location.href = PAGES_APIS.PAGES_LOGIN;
};

const goHome = () => {
  window.location.href = PAGES_APIS.PAGES_MAIN_MPMQR;
};

const goBack = () => {
  document;
  const referrer = document.referrer;
  const currentPath = window.location.pathname; // 현재 URL 경로 가져오기
  console.log('referrer:', referrer);

  if (currentPath.includes('/pages/auth')) {
    window.history.back();
    return; // 함수 종료
  }

  if (!referrer || referrer.includes(PAGES_APIS.PAGES_LOGIN)) {
    // 이전 페이지가 로그인 페이지라면 메인 홈으로 이동
    goHome();
  } else {
    window.history.back();
  }
};

const pageMove = (url) => {
  window.location.href = url;
};

const qr_debounce = (fn, ms) => {
  let timeoutId;
  return function (...args) {
    clearTimeout(timeoutId);
    timeoutId = setTimeout(() => fn.apply(this, args), ms);
  };
};

function qr_throttleLeading(func, wait) {
  let timer = null;

  return function (...args) {
    // 타이머가 없을 때만(즉, 처음이거나 대기가 끝났을 때만) 실행
    if (!timer) {
      func.apply(this, args);

      // 실행 후 타이머를 설정해 'wait' 시간 동안 차단
      timer = setTimeout(() => {
        timer = null;
      }, wait);
    }
  };
}

const PAGES_APIS = {
  PAGES_LOGIN: '/pages/login',
  PAGES_MAIN_MPMQR: '/pages/home/mpmqr',
  PAGES_NOTICE: '/pages/settings/notice',
  PAGES_GUIDE: '/pages/settings/guide',
  PAGES_TERMS_SERVICE: '/pages/settings/terms-service',
  PAGES_TERMS_SERVICE_TERMS: '/pages/settings/terms-service/terms',
  PAGES_TERMS_SERVICE_PERMISSIONS: '/pages/settings/terms-service/permissions',
  PAGES_TERMS_SERVICE_CANCEL: '/pages/settings/terms-service/cancel',
  PAGES_EMPLOYEE_ADD: '/pages/member/employee/add',
  PAGES_EMPLOYEE_LIST: '/pages/member/employee/list',
  PAGES_EMPLOYEE_PW_CHANGE: '/pages/member/{}/employee/change-pw',
  PAGES_MASTER_PW_CHANGE: '/pages/member/master/change-pw',
  PAGES_MERCHANT_VAT: '/pages/merchant/vat',
  PAGES_MERCHANT_TIP: '/pages/merchant/tip',
  PAGES_QRKIT_APPLY: '/pages/qrkit/apply',
  PAGES_QRKIT_STATUS: '/pages/qrkit/status',

  PAGES_SIGNUP_TERMS: '/pages/auth/signup/terms',
  PAGES_SIGNUP_AUTH_METHOD: '/pages/auth/signup/auth-method',
  PAGES_SIGNUP_MERCHANT: '/pages/auth/signup/merchant',
  PAGES_SIGNUP_MEMBER: '/pages/auth/signup/member',

  PAGES_FINDID_AUTH_METHOD: '/pages/auth/findid/auth-method',
  PAGES_FINDID_MERCHANT: '/pages/auth/findid/merchant',
  PAGES_FINDID_CONFIRM: '/pages/auth/findid/confirm',

  PAGES_FINDPW_CHECK_ID: '/pages/auth/findpw/check-id',
  PAGES_FINDPW_AUTH_METHOD: '/pages/auth/findpw/auth-method',
  PAGES_FINDPW_CHANGE: '/pages/auth/findpw/change',

  getPathVariableUrl: function (url, memberId) {
    return url.replace('{}', memberId);
  },
};

const REST_APIS = {
  MERCHANT: {
    INFO: '/qrpay/api/v1/merchant/info',
    EMPLOYEES: '/qrpay/api/v1/merchant/employees',
    ADD_EMPLOYEES: '/qrpay/api/v1/merchant/add-employee',
    MPMQR: '/qrpay/api/v1/merchant/mpmqr',
    CHANGE_NAME: '/qrpay/api/v1/merchant/change-name',
    CHANGE_TIP: '/qrpay/api/v1/merchant/change-tip',
    CHANGE_VAT: '/qrpay/api/v1/merchant/change-vat',
  },
  MEMBER: {
    ID_DUP_CHECK: '/qrpay/api/open/v1/member/id-check',
    EMPLOYEE_STATUS_CHANGE: '/qrpay/api/v1/member/{memberId}/employee-status-change',
    EMPLOYEE_PERMISSION_CANCEL_CHANGE: '/qrpay/api/v1/member/{memberId}/employee-cancel-permission-change',
    EMPLOYEE_CANCEL: '/qrpay/api/v1/member/{memberId}/employee-cancel',
    EMPLOYEES_CHAGE_PASSWORD: '/qrpay/api/v1/member/{memberId}/employee-password-change',
    MASTER_MEMBER_CHAGE_PASSWORD: '/qrpay/api/v1/member/{memberId}/password-change',
    getPathVariableUrl: function (url, memberId) {
      return url.replace('{memberId}', memberId);
    },
  },
  QR_KIT: {
    APPLY: '/qrpay/api/v1/qr-kit/apply',
    STATUS: '/qrpay/api/v1/qr-kit/status',
  },

  OPEN: {
    PW_RESET: '/qrpay/api/open/v1/member/password-reset',
    BC_MERCHANTS: '/qrpay/api/open/v1/bc/merchants',
  },
};
