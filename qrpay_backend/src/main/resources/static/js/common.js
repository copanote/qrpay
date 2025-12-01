// Chrome 36.0.0.0 down version check (36.0.0.0 is false)
var oldWeb = /Chrome\/([0-9]|[0-2][0-9]|3[0-5])\.[0-9]+\.[0-9]+\.[0-9]+/.test(window.navigator.userAgent);

// Scroll Move
function scrollMove(t,h,m){
	"use strict";
	if(h==undefined) h=0;
	if(m!=undefined && jQuery(window).width()<993) h=m;
		var o = jQuery('html, body');
	if(navigator.userAgent.toLowerCase().match(/trident/i)){
		o = jQuery('html');
	}
	o.animate({
		scrollTop:jQuery(t).offset().top-h
	},500);
}
// Menu Open
function menuOpen(o){
	"use strict";
	var o = $(o).attr('id'),
		a = -$(window).scrollTop();
	$('#wrap').css('top',a);
	$('body').addClass('gnb-open');
	$('body').addClass(o+'-open');
	if(oldWeb){
		$('#'+o).show();
	}
}

// Menu Close
function menuClose(o){
	'use strict';
	var o = $(o).attr('id'),
		originScroll = -$('#wrap').position().top;
	$('body').removeClass(o+'-open');
	$('body').removeClass('gnb-open');
	if (originScroll != -0) {
		$(window).scrollTop(originScroll);
	}
	$('#wrap').removeAttr('style');
	if(oldWeb){
		$('#'+o).hide();
	}
}

function comma(str){
	str = String(str);
	return str.replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,');
}
function uncomma(str){
	str = String(str);
	return str.replace(/[^\d]+/g, '');
}
function inputNumberFormat(obj) {
	obj.value = comma(uncomma(obj.value));
}
jQuery(function ($) {
	var $body = $('body'),
		$window = $(window);
	
	if(oldWeb){
		menuClose(gnb);
	}
	$('.js-mn').click(function(){
		if ($body.hasClass('gnb-open')) {
			menuClose(gnb);
		}else {
			menuOpen(gnb);
		}
	});
	
	$('#ct .animated').addClass('ani-stop');
	function scrollSection(){
		var sT = $window.scrollTop();
		var wH = $window.height();
		$('#wrap').find('.px-motion').each(function(){
			var t = $(this);
			var tT = t.offset().top;
			var tH = t.innerHeight();
			var tD = 90;

			if(t.attr('data-delay')){
				tD = t.attr('data-delay');
			}
			if(tT-wH<sT-tD && tT+tH>sT){
				t.find('.animated').removeClass('ani-stop');
				if(t.find('video').length){
					t.find('video')[0].play();
				}
			}else {
				t.find('.animated').addClass('ani-stop');
				if(t.find('video').length){
					t.find('video')[0].pause();
					t.find('video')[0].currentTime  = 0;
				}
			}
		});
	}

	var lastScrollTop = 0;
	$window.scroll(function(){
		var sT = $(this).scrollTop(),
			lnbPosY;
		
		if ($(this).scrollTop()>0) {
			$body.addClass('is-scroll');
		}else {
			$body.removeClass('is-scroll');
		}
		if ($('.px-motion').length) {
			scrollSection();
		}
		
		// up&down
		if(sT>lastScrollTop){
			$body.addClass('scroll-down');
			$body.removeClass('scroll-up');
		} else {
			$body.removeClass('scroll-down');
			$body.addClass('scroll-up');
		}
		lastScrollTop = sT;	
	}).trigger('scroll');
});