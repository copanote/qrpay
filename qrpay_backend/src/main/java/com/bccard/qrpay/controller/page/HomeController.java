package com.bccard.qrpay.controller.page;


import com.bccard.qrpay.auth.service.AuthService;
import com.bccard.qrpay.controller.page.dto.ResponseHomeMpmqr;
import com.bccard.qrpay.domain.common.code.MemberRole;
import com.bccard.qrpay.domain.member.Member;
import com.bccard.qrpay.domain.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/pages")
public class HomeController {

    private final AuthService authService;
    private final MemberService memberService;
//    private final Merc

    @GetMapping("/login")
    public String loginView(Model model) {
        model.addAttribute("data", "hello!!");
        return "/home/login";
    }

    @GetMapping("/home/mpmqr")
    public String home(
//            @CookieValue(value = "accessToken", required = false) String accessToken,
//            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            Model model,
            @CookieValue(value = "memId", required = false) String memId
    ) {

        if (memId == null || memId.isBlank()) {
            //Needs Authenticate
        }

        Member member = memberService.findBy(memId).orElseThrow();
        String merchantName = member.getMerchant().getMerchantName();
        String qrimage = "iVBORw0KGgoAAAANSUhEUgAAAMgAAADIAQAAAACFI5MzAAAD5ElEQVR42t2YPY6sOhBGCzlw1mwAydtwxpZgA/xsALZE5m0gsQHICBB+p+i+c/WCG3Td6D3Umu7pM8Km6quqzyP5T5f8R4mI3/a4TXG7vbzqSuI2LlIYSciJ9zzUOaftWK6uDmPKh5FURbpesuVl7aRq4tVEKZK0duLG7O4Ybr8W2c3L2v4VWftcsdmBdaSS+irthBi4fLohZlYbfNXFq/+JzteE/FRt+vfrJ3NfE71u3pI7sjvOq6nD8VtV3xIpljATyDPvGoNtTtLFbTaSjAzn8+rEzYkvUGI+FpeNZNvF3b561WGKuuuudpO/WiOpmvoqFpQobQqjBpiM6QcTcTz6S65X7cYl7DUiWpt6O4xkO043npeQlrSNJ+WbJ3FWwiJBY6D9gFSvfQq7v0ojkSaGcWGb/OT2hJaEb9lIVhGqbe2iNKzjCS3V/GjHQkI+rz5LmatyQT6CdublU8HfE+KXh0gFh5u0nPmu3e6fCrYQN9SaHLppJ7Rn0l6V6YmohVQ8erlsUx0ou/ncKOUpvruygUh/8vTa2m9Pj1+lpru40UraRHIo3LVMBOPqPJ3vKoykKjLNQPqEGFXgKqX0ZNtCKLX12TWldtGrMst+qtFAHg3GtSCcy6dX5XcMTKTXmqDHuEc72quO9PQqC2H+r/QqWgvr7F4aCdTcYSR5EH5bO69NCw2+okIrWVukLUxseifyuUR0SFqJG8mzqm8bE3XMDEdNVWkkPG5FtQ1enQWD6FXrOqORsGXWWYuknkKe2djIM80sBMeEdvLNy3+0c/OHRuIwdJNXZzfUzEm0Q9eveiOhOBj7a78Qy0ut01n1715lIarB6dn1FNVN7I8wDyNBehfhhMyJZoB2sGNmolOiTRsxoEoeX8yHqrAStNMv4T3EtIJ/9yoDwU0H7NKkQ0y9GFalfFewhehzayvlMFG/taMfrARd05zoBGHwuuv+cUCFkXAhwO0W6Tx+Rx3K/ONuvyb0eAY1Y8dNeFjOTF6jYiUIkCh+ds06BPV+x8BCAluetY6ZPNvOFPLYqKo1EvIcJuHGbscRa7rC5B8vZiEVgRxoA5EPH+/fvt26heiM7fDUhPZ0+JRRDVQerUS4Yhie4sA9dWp8nl1biFrCVvufameIzApCkmcrKRc9/naRIclA0xOPvN26hXChPm5P5Wm5SJ2PbCYca+grfKEjiGMEJ0V4ayQhP72kV/VxIP7MxtlI9MSPwelPaZ7ZSGg/MbCSyePF8Dt6e/X+HJ7+gtw1LvtxxPQq5na8WiMhBrpZPFSvXmx96aHnV3S+JpqfMeXxDBNjVtsMh6e1MJL/23/Z/nT9A1jjMlq1Y1v+AAAAAElFTkSuQmCC";

        ResponseHomeMpmqr responseHomeMpmqr = ResponseHomeMpmqr
                .builder()
                .merchantName(merchantName)
                .qrBase64Image(qrimage)
                .isAdmin(member.getRole() == MemberRole.MASTER)
                .build();

        model.addAttribute("responseHomeMpmqr", responseHomeMpmqr);

        return "home/mpmqr/main-mpmqr";
    }


}
