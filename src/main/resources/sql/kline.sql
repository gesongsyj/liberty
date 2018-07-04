#sql("paginate")
	select *
	from kline
	#set(flag=0)
	#if(qo.keyword)
		#(flag==0?"where":"and") id like concat('%',#para(qo.keyword),'%')
		#set(flag=1)
	#end
#end

#sql("getLastByCode")
	select *
	from kline k join currency c on k.currencyId=c.id
	#set(flag=0)
	#if(code)
		#(flag==0?"where":"and") c.code=#para(code)
		#set(flag=1)
	#end
	order by k.date asc
#end