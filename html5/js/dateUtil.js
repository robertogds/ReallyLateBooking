(function(){root.formatDate=function(){var a,b;a=String.formatDate(new Date);b=String.formatTime(new Date);return a+" "+b};root.getDayOfWeekString=function(a){switch(a.getDay()){case 1:return L("monday");case 2:return L("tuesday");case 3:return L("wednesday");case 4:return L("thursday");case 5:return L("friday");case 6:return L("saturday");case 0:return L("sunday")}};root.getShortMonthString=function(a){switch(a.getMonth()){case 0:return L("jan").substr(0,3);case 1:return L("feb").substr(0,3);case 2:return L("mar").substr(0,
3);case 3:return L("apr").substr(0,3);case 4:return L("may").substr(0,3);case 5:return L("jun").substr(0,3);case 6:return L("jul").substr(0,3);case 7:return L("aug").substr(0,3);case 8:return L("sep").substr(0,3);case 9:return L("oct").substr(0,3);case 10:return L("nov").substr(0,3);case 11:return L("dec").substr(0,3)}};root.getLocaleDateString=function(a){switch(a.getMonth()){case 0:return L("jan")+" "+a.getDate()+", "+a.getFullYear();case 1:return L("feb")+" "+a.getDate()+", "+a.getFullYear();case 2:return L("mar")+
" "+a.getDate()+", "+a.getFullYear();case 3:return L("apr")+" "+a.getDate()+", "+a.getFullYear();case 4:return L("may")+" "+a.getDate()+", "+a.getFullYear();case 5:return L("jun")+" "+a.getDate()+", "+a.getFullYear();case 6:return L("jul")+" "+a.getDate()+", "+a.getFullYear();case 7:return L("aug")+" "+a.getDate()+", "+a.getFullYear();case 8:return L("sep")+" "+a.getDate()+", "+a.getFullYear();case 9:return L("oct")+" "+a.getDate()+", "+a.getFullYear();case 10:return L("nov")+" "+a.getDate()+", "+
a.getFullYear();case 11:return L("dec")+" "+a.getDate()+", "+a.getFullYear()}}}).call(this);