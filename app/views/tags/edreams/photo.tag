	<div data-role="page" id="${_pageId}" data-theme="a" class="photos">
		<div data-role="header" data-id="headerNav" data-theme="b">
			<a  href="#${_returnPageId}" data-icon="arrow-l" class="alt ui-btn-left">Hotel</a>
			<h2>Photos</h2>
		</div>
		<div data-role="content" data-theme="a" id="photos">
			<img src="${_image}" alt="">
			<ul data-role="pagination">
				<li class="ui-pagination-prev"><a href="#${_prevPageId}">Prev</a></li>
				<li class="ui-pagination-next"><a href="#${_nextPageId}">Next</a></li>
			</ul>
		</div>
		<div data-role="footer" class="ui-bar" data-id="bookingFooter" data-position="fixed" data-theme="b">
			<a href="index.html" data-role="button" data-inline="false" >Book a room</a>
		</div>
	</div>