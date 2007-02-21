INSERT INTO DEFAULT_TABLE (
    /*IF dto.aaa != null*/AAA,/*END*/
    /*IF dto.bbb != null*/BBB,/*END*/
    VERSION_NO
) VALUES (
	 /* パート */
	/*IF dto.aaa != null*//*dto.aaa*/null,/*END*/
	/*IF dto.bbb != null*//*dto.bbb*/null,/*END*/
	0
	 /* コメント中の? */
)
