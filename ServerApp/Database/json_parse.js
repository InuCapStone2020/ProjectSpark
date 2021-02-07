var json = '{"NUM":11111, "SUBNUM":2, "M_DATE":"2020-03-09", "M_TIME":"03:09:39", "REGION":"인천광역시 연수구", "CONTENT":"COVID-19"}';
var obj = JSON.parse(json);

console.log(obj.NUM);
console.log(obj.SUBNUM);
console.log(obj.M_DATE);
console.log(obj.M_TIME);
console.log(obj.REGION);
console.log(obj.CONTENT);