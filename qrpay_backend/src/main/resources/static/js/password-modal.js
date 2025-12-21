const PasswordModal = {
  currentId: null,
  onSuccess: null, // 성공 시 실행할 함수 저장소
  onCancel: null, // 취소/닫기 시 실행할 함수 저장소
  password: '',
  maxLen: 6,

  // open 함수에서 콜백들을 받음
  open: function ({ loginId, onSuccessCallback, onCancelCallback }) {
    this.currentId = loginId;
    this.onSuccess = onSuccessCallback;
    this.onCancel = onCancelCallback;

    $('#pw-modal-loginId').text(this.currentId);
    $('#passwordModal').show(); // 또는 fadeIn
  },

  // [중요] 인증 버튼을 눌렀을 때 실행되는 함수
  verify: function () {
    qrpaySdk
      .authenticate(this.currentId, this.password)
      .then((data) => {
        console.log('인증 응답:', data);

        if (data.ok) {
          // 토큰이 SDK 내부에 저장됨
          console.log('로그인 성공');

          // 1. 인증 성공 시: 모달 닫고 성공 콜백 호출
          this.success_close();
        } else {
          const errorMsg = data.message || data.error?.message || '로그인에 실패했습니다.';
          alert(errorMsg);
          this.clearAll();
        }
      })
      .catch((error) => {
        console.error('로그인 오류:', error);
        alert('로그인 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
      })
      .finally(() => {
        // 버튼 상태 복구
        // btnLogin.disabled = false;
        // btnLogin.textContent = originalText;
      });
  },

  success_close: function () {
    $('#passwordModal').hide();
    // 닫을 때 취소 콜백이 있다면 실행
    if (typeof this.onSuccess === 'function') this.onSuccess(true);

    // 메모리 누수 방지를 위해 콜백 비우기
    this.onSuccess = null;
    this.onCancel = null;
    this.clearAll();
  },

  close: function () {
    $('#passwordModal').hide();
    // 닫을 때 취소 콜백이 있다면 실행
    if (typeof this.onCancel === 'function') this.onCancel();

    // 메모리 누수 방지를 위해 콜백 비우기
    this.onSuccess = null;
    this.onCancel = null;
    this.clearAll();
  },

  pushKey: function (num) {
    if (this.password.length < this.maxLen) {
      this.password += num;
      this.updateDots();

      // 6자리 다 입력되면 자동 검증
      if (this.password.length === this.maxLen) {
        this.verify();
      }
    }
  },

  // 지우기 버튼 클릭 시 호출
  deleteKey: function () {
    this.password = this.password.slice(0, -1);
    this.updateDots();
  },

  clearAll: function () {
    this.password = '';
    this.updateDots();
  },

  // UI 점(dot) 업데이트
  updateDots: function () {
    const dots = $('#pw-dots span');
    dots.removeClass('active');
    for (let i = 0; i < this.password.length; i++) {
      $(dots[i]).addClass('active');
    }
  },
};
