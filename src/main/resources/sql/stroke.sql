#sql("paginate")
	select *
	from stroke
	#set(flag=0)
	#if(qo.keyword)
		#(flag==0?"where":"and") id like concat('%',#para(qo.keyword),'%')
		#set(flag=1)
	#end
#end

#sql("getLast")
	select *
	from stroke
	order by endDate desc
#end

#sql("listAllByCode")
	select s.*
	from stroke s join currency c on s.currencyId=c.id
	#set(flag=0)
	#if(code)
		#(flag==0?"where":"and") c.code = #para(code)
		#set(flag=1)
	#end
	#if(type)
		#(flag==0?"where":"and") s.type = #para(type)
		#set(flag=1)
	#end
	order by date asc
#end

#sql("getListByDate")
	select s.*
	from stroke s join currency c on s.currencyId=c.id
	#set(flag=0)
	#if(date)
		#(flag==0?"where":"and") s.startDate >= #para(date)
		#set(flag=1)
	#end
	#if(code)
		#(flag==0?"where":"and") c.code = #para(code)
		#set(flag=1)
	#end
	#if(type)
		#(flag==0?"where":"and") s.type = #para(type)
		#set(flag=1)
	#end
	order by date asc
#end