select * from customer
/*BEGIN*/where
/*IF condition.startDate != null*/
	DATE_OF_JOIN >= /*condition.startDate*/'2000/04/01'
/*END*/
/*END*/
order by /*$condition.sortKey*/SORT_ORDER2
