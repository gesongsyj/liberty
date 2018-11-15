#sql("paginate")
	select *
	from kline
	#set(flag=0)
	#if(qo.keyword)
		#(flag==0?"where":"and") id like concat('%',#para(qo.keyword),'%')
		#set(flag=1)
	#end
#end

#sql("getLastOneByCode")
	select k.*
	from kline k join currency c on k.currencyId=c.id
	#set(flag=0)
	#if(code)
		#(flag==0?"where":"and") c.code=#para(code)
		#set(flag=1)
	#end
	#if(type)
		#(flag==0?"where":"and") k.type=#para(type)
		#set(flag=1)
	#end
	order by k.date desc
#end

#sql("getLastByCode")
	select k.*
	from kline k join currency c on k.currencyId=c.id
	#set(flag=0)
	#if(code)
		#(flag==0?"where":"and") c.code=#para(code)
		#set(flag=1)
	#end
	#if(type)
		#(flag==0?"where":"and") k.type=#para(type)
		#set(flag=1)
	#end
	order by k.date desc
	limit 0,2
#end

#sql("listAllByCode")
	select k.*
	from kline k join currency c on k.currencyId=c.id
	#set(flag=0)
	#if(code)
		#(flag==0?"where":"and") c.code = #para(code)
		#set(flag=1)
	#end
	#if(type)
		#(flag==0?"where":"and") k.type = #para(type)
		#set(flag=1)
	#end
	order by date asc
#end

#sql("getListByDate")
	select k.*
	from kline k join currency c on k.currencyId=c.id
	#set(flag=0)
	#if(date)
		#(flag==0?"where":"and") k.date >= #para(date)
		#set(flag=1)
	#end
	#if(code)
		#(flag==0?"where":"and") c.code = #para(code)
		#set(flag=1)
	#end
	#if(type)
		#(flag==0?"where":"and") k.type = #para(type)
		#set(flag=1)
	#end
	order by date asc
#end

#sql("getByDateRange")
	select k.*
	from kline k join currency c on k.currencyId=c.id
	#set(flag=0)
	#if(startDate)
		#(flag==0?"where":"and") k.date >= #para(startDate)
		#set(flag=1)
	#end
	#if(endDate)
		#(flag==0?"where":"and") k.date <= #para(endDate)
		#set(flag=1)
	#end
	#if(code)
		#(flag==0?"where":"and") c.code = #para(code)
		#set(flag=1)
	#end
	#if(type)
		#(flag==0?"where":"and") k.type = #para(type)
		#set(flag=1)
	#end
	order by date asc
#end

#sql("listAll")
	select *
	from kline
	order by date asc
#end

#sql("getByCurrencyId")
	select *
	from kline 
	#set(flag=0)
	#if(currencyId)
		#(flag==0?"where":"and") currencyId >= #para(currencyId)
		#set(flag=1)
	#end
#end

