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
	from line
	order by endDate desc
#end

#sql("getLastByCode")
	select l.*
	from line l join currency c on l.currencyId=c.id
	#set(flag=0)
	#if(code)
		#(flag==0?"where":"and") c.code = #para(code)
		#set(flag=1)
	#end
	#if(type)
		#(flag==0?"where":"and") l.type = #para(type)
		#set(flag=1)
	#end
	order by endDate desc
#end

#sql("listAllByCode")
	select l.*
	from line l join currency c on l.currencyId=c.id
	#set(flag=0)
	#if(code)
		#(flag==0?"where":"and") c.code = #para(code)
		#set(flag=1)
	#end
	#if(type)
		#(flag==0?"where":"and") l.type = #para(type)
		#set(flag=1)
	#end
	order by startDate asc
#end

#sql("listAll")
	select *
	from line
	order by endDate asc
#end