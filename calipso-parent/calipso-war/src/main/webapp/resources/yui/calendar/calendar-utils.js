var singletonCal;

function handleSelect(type, args, calTxt) {
    var dates = args[0]; var date = dates[0];
    var year = date[0], month = date[1], day = date[2];
    txtField = document.getElementById(calTxt[1]);
    //txtField.value = year + '-' + month + '-' + day;
    txtField.value = day + '/' + month + '/' + year;
    calShowing = false;
    calTxt[0].hide();
    singletonCal = null;
}

function showCalendar(cal, txtId) {
    if(singletonCal) {                        
        singletonCal.hide();
        if(singletonCal == cal) {
            singletonCal = null;
            return;
        }
    }
    
    txtField = document.getElementById(txtId);                    
    if(txtField.value) {                        
        //var a = txtField.value.split('-'); //2008-02-28 
        var a = txtField.value.split('/'); //28/02/2008
        //var date = new Date(a[0], a[1] - 1, a[2]);
        var date = new Date(a[2], a[1] - 1, a[0]);
        if(date.valueOf() + '' == 'NaN') {
            return;
        }
        cal.select(date);
        //cal.cfg.setProperty('pagedate', a[1] + '/' + a[0]);
        cal.cfg.setProperty('pagedate', a[1] + '/' + a[2]);
    }
    cal.render();                    
    cal.show();
    singletonCal = cal;
}