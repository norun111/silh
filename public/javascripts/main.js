function calculate(){
    let sleepTime = document.form1.sleepTime.options[document.form1.sleepTime.selectedIndex].value;

    let workTime = document.form1.workTime.options[document.form1.workTime.selectedIndex].value;

    let otherTime = document.form1.otherTime.options[document.form1.otherTime.selectedIndex].value;

    let total = parseInt(sleepTime) + parseInt(workTime) + parseInt(otherTime);

    document.form1.field_total.value = total;
}