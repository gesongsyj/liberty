#sql("paginate")
	select *
	from stroke
	#set(flag=0)
	#if(qo.keyword)
		#(flag==0?"where":"and") id like concat('%',#para(qo.keyword),'%')
		#set(flag=1)
	#end
#end

#sql("getLastByCode")
	select s.*
	from currency c STRAIGHT_JOIN stroke s on s.currencyId=c.id
	#set(flag=0)
	#if(code)
		#(flag==0?"where":"and") c.code = #para(code)
		#set(flag=1)
	#end
	#if(type)
		#(flag==0?"where":"and") s.type = #para(type)
		#set(flag=1)
	#end
	order by endDate desc
#end

#sql("listAllByCode")
	select s.*
	from currency c STRAIGHT_JOIN stroke s on s.currencyId=c.id
	#set(flag=0)
	#if(code)
		#(flag==0?"where":"and") c.code = #para(code)
		#set(flag=1)
	#end
	#if(type)
		#(flag==0?"where":"and") s.type = #para(type)
		#set(flag=1)
	#end
	order by startDate asc
#end

#sql("getListByDate")
	select s.*
	from currency c STRAIGHT_JOIN stroke s on s.currencyId=c.id
	#set(flag=0)
	#if(code)
		#(flag==0?"where":"and") c.code = #para(code)
		#set(flag=1)
	#end
	#if(date)
		#(flag==0?"where":"and") s.endDate > #para(date)
		#set(flag=1)
	#end
	#if(type)
		#(flag==0?"where":"and") s.type = #para(type)
		#set(flag=1)
	#end
	order by startDate asc
#end

#sql("getByDateRange")
	select s.*
	from stroke s
	#set(flag=0)
	#if(startDate)
		#(flag==0?"where":"and") s.startDate >= #para(startDate)
		#set(flag=1)
	#end
	#if(endDate)
		#(flag==0?"where":"and") s.endDate <= #para(endDate)
		#set(flag=1)
	#end
	#if(currencyId)
		#(flag==0?"where":"and") s.currencyId = #para(currencyId)
		#set(flag=1)
	#end
	#if(type)
		#(flag==0?"where":"and") s.type = #para(type)
		#set(flag=1)
	#end
	order by startDate desc
#end

#sql("listAll")
	select *
	from stroke
	order by startDate asc
#end