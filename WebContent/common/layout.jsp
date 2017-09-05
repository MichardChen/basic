<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<script id="sidebar_tpl" type="text/html">
<nav class="navbar-default navbar-static-side" role="navigation" style="margin-top:40px;">
	<div class="sidebar-collapse" style="background-color: #293038">
		<ul class="nav" id="side-menu">
			  <li class="nav-header">
            	<div>
                	
          		</div>
         	 </li>
			{{# for(var i = 0, len = d.menu.length; i < len; i++){ }}
			 <li id="{{d.menu[i].menu_id}}" class="menu">
             	<a href="${CONTEXT_PATH}{{ d.menu[i].url }}?menu_id={{d.menu[i].menu_id}}"><i class="fa {{ d.menu[i].icon }}"></i><span class="nav-label">{{ d.menu[i].menu_name }}</span>
			{{# if(d.menu[i].children && d.menu[i].children.length>0){ }}
				<span class="fa arrow"></span>
			{{# } }}</a>
                     {{# if(d.menu[i].children && d.menu[i].children.length>0){ }}
			<ul class="nav nav-second-level">
				{{# for(var j=0; j<d.menu[i].children.length; j++){ }}
                         <li class="menu">
                             <a href="${CONTEXT_PATH}{{ d.menu[i].children[j].url }}">{{ d.menu[i].children[j].menu_name }}
					{{# if(d.menu[i].children[j].children && d.menu[i].children[j].children.length>0){ }}
						<span class="fa arrow"></span>
					{{# } }}</a>
					{{# if(d.menu[i].children[j].children && d.menu[i].children[j].children.length>0){ }}
                             <ul class="nav nav-third-level">
						{{# for(var k=0; k<d.menu[i].children[j].children.length; k++){ }}
                                 <li class="menu">
                                     <a href="${CONTEXT_PATH}{{ d.menu[i].children[j].children[k].url }}">{{ d.menu[i].children[j].children[k].menu_name }}</a>
                                 </li>
						{{# } }}
                             </ul>
					{{# } }}
                         </li>
				{{# } }}
                     </ul>
			{{# } }}
                 </li>
		{{# } }}
		</ul>
	</div>
</nav>
</script>

<script id="navbar_tpl" type="text/html">
<nav class="navbar navbar-default navbar-fixed-top">
<div class="wrapper page-heading" style="height:50px;font-size:14px;width:100%;background-color:#0099CC;">
		 <div class="" style="float:left;margin-top:-3px;">
			<div class="navbar-header" style="margin-top:-13px;display:inline-block;">
               	<a class="navbar-minimalize minimalize-styl-2 btn btn-primary " href="javascript:void(0)"><i class="fa fa-bars"></i> </a>
          	</div>
				<div class="" style="display:inline-block;margin-left:20px;">
				</div>
				<div style="display:inline-block;color:white;font-size:20px;">同记工作平台</div>
				<div class="btn-group" style="display:inline-block;margin-left:150px;font-size:15px;margin-top:-10px;">
					<a data-toggle="dropdown" class="dropdown-toggle" id="dropdownMenu">
               		<span class="clear"> <span class="block m-t-xs"> <strong class="font-bold" style="color:white;">{{ d.username }}{{# if(d.realname==null){ }}{{# }else{ }}{{ d.realname }}{{# } }}</strong><b class="caret" style="border-top:4px dashed #fff;"></b></span>
              	</a>
                <ul class="dropdown-menu animated fadeInRight m-t-xs" aria-labelledby="dropdownMenu">
                    <li><a href="${CONTEXT_PATH}/login/checkout">安全退出</a></li>
                </ul>
				</div>
		 </div> 
   </div>
   </nav>
</script>
<script id="navbar_title" type="text/html">
		
	

   
  
</script>
<script id="footer_tpl" type="text/html">
	
</script>

<!-- Mainly scripts -->
<script src="${CONTEXT_PATH}/assets/lib/jquery-2.1.1.min.js"></script>
<script src="${CONTEXT_PATH}/assets/lib/bootstrap.min.js?v=3.4.0"></script>

<script src="${CONTEXT_PATH}/assets/lib/metisMenu/metisMenu.min.js"></script>
<script src="${CONTEXT_PATH}/assets/lib/slimscroll/jquery.slimscroll.min.js"></script>
<script src="${CONTEXT_PATH}/assets/lib/laytpl/laytpl.js"></script>

<!-- Custom and plugin javascript 
<script src="${CONTEXT_PATH}/assets/js/hdl.js?v=2.2.0"></script>-->
<script src="${CONTEXT_PATH}/assets/js/hdlUtil.js"></script>

<script type="text/javascript">
	(function($) {
		$(function() {
			$.ajax({
				url : "${CONTEXT_PATH}/homepage/nav",
				dataType : "json",
				success : function(resp) {
					var navData = resp;
					var transMenu = hdlUtil.transformToTreeFormat({
						idKey : "menu_id",
						pIdKey : "p_menu_id",
						childKey : "children"
					}, resp.menu);
					navData.menu = transMenu;
					
					var gettpl = $('#sidebar_tpl').html();//左侧菜单栏
				
					var toptpl1 = $('#navbar_tpl').html();//上部导航栏
					
	
					laytpl(gettpl).render(navData, function(html) {
						$('#wrapper').prepend(html);
						setup();
						
					});
					
					laytpl(toptpl1).render(navData, function(html) {
						$('#wrapper').prepend(html);
		
					});
					$('.dropdown-toggle').dropdown();
					
					// minimalize menu
				    $('.navbar-minimalize').click(function () {
				        $("body").toggleClass("mini-navbar");
				        SmoothlyMenu();
				    });
				}
			});
			
			
		    $(window).bind("load resize", function() {
		        if ($(this).width() < 769) {
		            $('body').addClass('body-small');
		        } else {
		            $('body').removeClass('body-small');
		        }
		    })
		    
		});
		
		
		function setup(){
	        if($("body").hasClass('fixed-sidebar')) {
	            $('.sidebar-collapse').slimScroll({
	                height: '100%',
	                railOpacity: 0.5,
	           });
	       }
			
			$('#side-menu>li:not(:first) a').each(function(){
				if($(this).attr('href')!=''){
					if(window.location.href.indexOf($(this).attr('href'))>=0){
					
						$(this).parents('li').addClass('active');
						return false;
					}
				}
			});
			
			var bcData = [{name:'', url:''}];
			$('#side-menu .active').each(function(){
				if($(this).children('a').attr('href') != bcData[0].url){
					var obj = {};
					obj.name = $(this).children('a').text();
					obj.url = $(this).children('a').attr('href');
					bcData.push(obj);
				}
			});
			
			
			var toptpl = $('#navbar_title').html();
			var topHtml = laytpl(toptpl).render(bcData);
			$('#page-wrapper').prepend(topHtml);
			
			var footertpl = $('#footer_tpl').html();
			var footerHtml = laytpl(footertpl).render({});
			$('#page-wrapper').append(footerHtml);
			
			
			
			
			$('#side-menu').metisMenu();
		}
		
		function SmoothlyMenu() {
		    if (!$('body').hasClass('mini-navbar') || $('body').hasClass('body-small')) {
		        // Hide menu in order to smoothly turn on when maximize menu
		        $('#side-menu').hide();
		        // For smoothly turn on menu
		        setTimeout(
		            function () {
		                $('#side-menu').fadeIn(500);
		            }, 100);
		    } else if ($('body').hasClass('fixed-sidebar')){
		        $('#side-menu').hide();
		        setTimeout(
		            function () {
		                $('#side-menu').fadeIn(500);
		            }, 300);
		    } else {
		        // Remove all inline style from jquery fadeIn function to reset menu state
		        $('#side-menu').removeAttr('style');
		    }
		}
	
	})(jQuery);
</script>
