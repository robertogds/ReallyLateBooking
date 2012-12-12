define(["Ti/_/Layouts/Base","Ti/_/declare","Ti/UI","Ti/_/lang","Ti/_/style"],function(y,z,n,A){var r=A.isDef,v=Math.round;return z("Ti._.Layouts.Vertical",y,{_doLayout:function(c,d,b,u,o){for(var h={width:0,height:0},s=c._children,a,k=0,i,l,g,j,m,f,p,q=[],e=0,t=s.length,r=this._measureNode,k=0;k<t;k++)a=c._children[k],!a._alive||!a.domNode?this.handleInvalidState(a,c):(a._measuredRunningHeight=e,a._markedForLayout&&((a._preLayout&&a._preLayout(d,b,u,o)||a._needsMeasuring)&&r(a,a,a._layoutCoefficients,
this),i=a._layoutCoefficients,l=i.width,g=i.height,j=i.sandboxWidth,m=i.sandboxHeight,f=i.left,i=i.top,l=l.x1*d+l.x2,g=g.x1*b+g.x2*(b-e)+g.x3,p=a._getContentSize?a._getContentSize(l,g):a._layout._doLayout(a,isNaN(l)?d:l-a._borderLeftWidth-a._borderRightWidth,isNaN(g)?b:g-a._borderTopWidth-a._borderBottomWidth,isNaN(l),isNaN(g)),isNaN(l)&&(l=p.width+a._borderLeftWidth+a._borderRightWidth),isNaN(g)&&(g=p.height+a._borderTopWidth+a._borderBottomWidth),a._measuredWidth=l,a._measuredHeight=g,u&&0!==f.x1?
q.push(a):(f=a._measuredLeft=f.x1*d+f.x2*l+f.x3,j=a._measuredSandboxWidth=j.x1*d+j.x2+l+(isNaN(f)?0:f),j>h.width&&(h.width=j)),a._measuredTop=i.x1*b+i.x2+e,a._measuredSandboxHeight=m.x1*b+m.x2+g),e=h.height+=a._measuredSandboxHeight);t=q.length;for(k=0;k<t;k++)a=q[k],j=a._layoutCoefficients.sandboxWidth,j=a._measuredSandboxWidth=j.x1*d+j.x2+a._measuredWidth,j>h.width&&(h.width=j);for(k=0;k<t;k++)a=q[k],f=a._layoutCoefficients.left,l=a._measuredWidth,j=a._measuredSandboxWidth,j>h.width&&(h.width=j),
f=a._measuredLeft=f.x1*h.width+f.x2*l+f.x3,a._measuredSandboxWidth+=isNaN(f)?0:f;t=s.length;for(k=0;k<t;k++)a=s[k],a._markedForLayout&&(n._elementLayoutCount++,a=s[k],c=a.domNode.style,c.zIndex=a.zIndex,c.left=v(a._measuredLeft)+"px",c.top=v(a._measuredTop)+"px",c.width=v(a._measuredWidth-a._borderLeftWidth-a._borderRightWidth)+"px",c.height=v(a._measuredHeight-a._borderTopWidth-a._borderBottomWidth)+"px",a._markedForLayout=!1,a.fireEvent("postlayout"));return this._computedSize=h},_getWidth:function(c,
d){!r(d)&&2>r(c.left)+r(c.center&&c.center.x)+r(c.right)&&(d=c._defaultWidth);return d===n.INHERIT?c._parent._parent?c._parent._parent._layout._getWidth(c._parent,c._parent.width)===n.SIZE?n.SIZE:n.FILL:n.FILL:d},_getHeight:function(c,d){!r(d)&&(d=c._defaultHeight);return d===n.INHERIT?c._parent._parent?c._parent._parent._layout._getHeight(c._parent,c._parent.height)===n.SIZE?n.SIZE:n.FILL:n.FILL:d},_isDependentOnParent:function(c){c=c._layoutCoefficients;return!isNaN(c.width.x1)&&0!==c.width.x1||
!isNaN(c.height.x1)&&0!==c.height.x1||!isNaN(c.height.x2)&&0!==c.height.x2||0!==c.sandboxWidth.x1||0!==c.sandboxHeight.x1||0!==c.left.x1||0!==c.top.x1},_doAnimationLayout:function(c,d){var b=c._parent._measuredWidth,n=c._parent._measuredHeight,o=c._measuredRunningHeight,h=d.width.x1*b+d.width.x2;return{width:h,height:d.height.x1*n+d.height.x2*(n-o)+d.height.x3,left:d.left.x1*b+d.left.x2*h+d.left.x3,top:d.top.x1*n+d.top.x2+o}},_measureNode:function(c,d,b,u){c._needsMeasuring=!1;var o=u.getValueType,
h=u.computeValue,s=u._getWidth(c,d.width),a=o(s),s=h(s,a),k=u._getHeight(c,d.height),c=o(k),k=h(k,c),i=d.left,l=o(i),i=h(i,l),g=d.center&&d.center.x,j=o(g),g=h(g,j),m=d.right,f=o(m),m=h(m,f),p=d.top,q=o(p),p=h(p,q),d=d.bottom,o=o(d),h=h(d,o),e,t=b.width,d=b.height,r=b.sandboxWidth,v=b.sandboxHeight,w=b.left,x=b.top,b=e=0;a===n.SIZE?b=e=NaN:a===n.FILL?(b=1,"%"===l?b-=i:"#"===l?e=-i:"%"===f?b-=m:"#"===f&&(e=-m)):"%"===a?b=s:"#"===a?e=s:"%"===l?"%"===j?b=2*(g-i):"#"===j?(b=-2*i,e=2*g):"%"===f?b=1-i-
m:"#"===f&&(b=1-i,e=-m):"#"===l?"%"===j?(b=2*g,e=-2*i):"#"===j?e=2*(g-i):"%"===f?(b=1-m,e=-i):"#"===f&&(b=1,e=-m-i):"%"===j?"%"===f?b=2*(m-g):"#"===f&&(b=-2*g,e=2*m):"#"===j&&("%"===f?(b=2*m,e=-2*g):"#"===f&&(e=2*(m-g)));t.x1=b;t.x2=e;r.x1="%"===f?m:0;r.x2="#"===f?m:0;b=e=a=0;c===n.SIZE?b=e=a=NaN:c===n.FILL?(e=1,"%"===q&&(b=-p),"#"===q&&(a=-p),"%"===o&&(b=-h),"#"===o&&(a=-h)):"%"===c?b=k:"#"===c&&(a=k);d.x1=b;d.x2=e;d.x3=a;b=e=0;"%"===q&&(b=p);"#"===q&&(e=p);"%"===o&&(b+=h);"#"===o&&(e+=h);v.x1=b;
v.x2=e;b=e=a=0;if("%"===l)b=i;else if("#"===l)a=i;else if("%"===j)b=g,e=-0.5;else if("#"===j)e=-0.5,a=g;else if("%"===f)b=1-m,e=-1;else if("#"===f)b=1,e=-1,a=-m;else switch(u._defaultHorizontalAlignment){case "center":b=0.5;e=-0.5;break;case "end":b=1,e=-1}w.x1=b;w.x2=e;w.x3=a;x.x1="%"===q?p:0;x.x2="#"===q?p:0},_defaultHorizontalAlignment:"center",_defaultVerticalAlignment:"start"})});