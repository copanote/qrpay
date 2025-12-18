package com.bccard.qrpay.domain.member;

import com.bccard.qrpay.auth.service.CustomPasswordEncoder;
import com.bccard.qrpay.domain.common.code.MemberRole;
import com.bccard.qrpay.domain.common.code.MemberStatus;
import com.bccard.qrpay.domain.member.repository.MemberQueryRepository;
import com.bccard.qrpay.domain.member.repository.MemberRepository;
import com.bccard.qrpay.domain.merchant.Merchant;
import com.bccard.qrpay.exception.MemberException;
import com.bccard.qrpay.exception.code.QrpayErrorCode;
import com.bccard.qrpay.utils.IdValidator;
import com.bccard.qrpay.utils.PasswordValidator;
import com.bccard.qrpay.utils.security.HashCipher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberQueryRepository memberQueryRepository;
    private final MemberRepository memberCUDRepository;
    private final CustomPasswordEncoder customPasswordEncoder;


    public Member findByMemberId(String memberId) {
        return memberQueryRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(QrpayErrorCode.MEMBER_NOT_FOUND));
    }

    public Optional<Member> findBy(String memberId) {
        return memberQueryRepository.findById(memberId);
    }


    public Member findMyLoginId(String loginId) {
        return memberQueryRepository.findByLoginId(loginId)
                .orElseThrow(() -> new MemberException(QrpayErrorCode.MEMBER_NOT_FOUND));
    }

    public boolean exist(String loginId) {
        return memberQueryRepository.findByLoginId(loginId).isPresent();
    }

    @Transactional
    public void passwordFail(String memberId) {
        Member member = findByMemberId(memberId);
        member.onPasswordFail();
    }

    public String hashPassword(String password) {
        return HashCipher.sha256EncodedBase64(password);
    }


    public String createNewMemberId() {
        Long seq = memberQueryRepository.getNextSequenceValue();
        String pad = StringUtils.leftPad(seq.toString(), 8, '0');
        return "";
    }

    public List<Member> findMemberByRole(Merchant merchant, MemberRole role) {
        List<Member> allMembers = memberQueryRepository.findAllMembers(merchant);
        return allMembers.stream()
                .filter(member -> member.getRole() == role)
                .filter(member -> member.getStatus() != MemberStatus.CANCELLED)
                .toList();
    }

    public List<Member> findMembers(Merchant merchant) {
        return memberQueryRepository.findAllMembers(merchant)
                .stream()
                .filter(member -> member.getStatus() != MemberStatus.CANCELLED)
                .toList();
    }

    @Transactional
    public Member updatePermissionToCancel(Member member, boolean permissionToCancel) {
        Member m = memberQueryRepository.findById(member.getMemberId()).orElseThrow(
                () -> new MemberException(QrpayErrorCode.MEMBER_NOT_FOUND)
        );

        boolean nowPermission = m.getPermissionToCancel() != null && m.getPermissionToCancel();

        if (nowPermission != permissionToCancel) {
            m.updatePermissionToCancel(permissionToCancel);
        }
        return m;
    }

    @Transactional
    public Member updateStatus(Member member, MemberStatus status) {
        Member m = memberQueryRepository.findById(member.getMemberId()).orElseThrow(
                () -> new MemberException(QrpayErrorCode.MEMBER_NOT_FOUND)
        );

        if (m.getStatus() != MemberStatus.CANCELLED && m.getStatus() != status) {
            m.updateStatus(status);
        }
        return m;
    }


    @Transactional
    public Member updatePassword(Member member, String password) {
        Member m = memberQueryRepository.findById(member.getMemberId()).orElseThrow(
                () -> new MemberException(QrpayErrorCode.MEMBER_NOT_FOUND)
        );

        if (!PasswordValidator.isValid(password)) {
            throw new MemberException(QrpayErrorCode.PASSWORD_POLICY_VIOLATION);
        }

        String toChangePassword = customPasswordEncoder.encode(password);

        if (m.getHashedPassword().equals(toChangePassword)) {
            throw new MemberException(QrpayErrorCode.DISALLOW_CURRENT_PASSWORD_REUSE);
        }

        m.updatePassword(customPasswordEncoder.encode(password));
        return m;
    }

    @Transactional
    public Member updatePassword(Member member, String currentPassword, String encodedPassword) {
        Member m = memberQueryRepository.findById(member.getMemberId()).orElseThrow(
                () -> new MemberException(QrpayErrorCode.MEMBER_NOT_FOUND)
        );

        if (!m.getHashedPassword().equals(customPasswordEncoder.encode(currentPassword))) {
//            throw new
        }

        m.updatePassword(encodedPassword);
        return m;
    }


    @Transactional
    public Member addEmployee(Merchant merchant, String employeeLoginId, String password, boolean permissionToCancel) {


        if (!IdValidator.isValid(employeeLoginId)) {
            throw new MemberException(QrpayErrorCode.LOGIN_ID_POLICY_VIOLATION);
        }

        if (exist(employeeLoginId)) {
            throw new MemberException(QrpayErrorCode.LOGIN_ID_CONFLICT);
        }

        if (!PasswordValidator.isValid(password)) {
            throw new MemberException(QrpayErrorCode.PASSWORD_POLICY_VIOLATION);
        }

        String newMemberId = createNewMemberId();
        Member newMem = Member.createEmployee()
                .merchant(merchant)
                .memberId(newMemberId)
                .loginId(employeeLoginId)
                .hashedPassword(customPasswordEncoder.encode(password))
                .permissionToCancel(permissionToCancel)
                .build();

        return memberCUDRepository.save(newMem);
    }


}
