import static org.garage48.tellme.DataService.*
import ratpack.groovy.templating.*

import groovy.json.JsonSlurper
import org.garage48.tellme.*
import ratpack.groovy.templating.*

import static ratpack.groovy.Groovy.*

ratpack {

  handlers {
	  	  
	//////////////////////////////////////////////
	// Web interface
	//////////////////////////////////////////////
	  
	get {
		render groovyTemplate('landing.html')
	}
	  
	get("question/random") {
		render groovyTemplate('answer.html', question: getRandomQuestion())
	}		

	get("question/ask") {
		render groovyTemplate('ask.html')
	}

	get("questions/:id") {
		def id = DataService.getPathId(request.path)
		render groovyTemplate('answer.html', question: getQuestion(id))
	}

	get("profile") {
		render groovyTemplate('profile.html')
	}

	get("api") {
		render groovyTemplate('api.html')
	}

	//////////////////////////////////////////////
	// API 
	//////////////////////////////////////////////

    get("api/questions/random/:amount") {
		int amount = 5
		def amountString = DataService.getPathId(request.path)
		if (amountString) {
			amount = amountString.toInteger()
		}
		render getJsonRandomQuestions(amount)  
	}

	get("api/questions/random") {
		redirect "random/5"
	}

	post("api/questions/new") {
		def question = new JsonSlurper().parseText(request.text)
		println "Question to insert: ${question}" 
		insertQuestion(question)
		render '{ "response": "OK" }'
	}

	get('api/question/:id') { 
		def idString = DataService.getPathId(request.path)
		response.send('application/json', getJsonQuestion(idString))
	}

	delete('api/question/:id') {
		def id = DataService.getPathId(request.path)
		deleteQuestion(id)
		render '{ "response": "OK" }'
	}

	put('api/question/:id') {
		def id = DataService.getPathId(request.path)
		updateQuestion(id)
		render '{ "response": "OK" }'
	}

	post("api/answers/new") {
		def answer = new JsonSlurper().parseText(request.text)
		println "Answer to insert: ${answer}"
		insertAnswer(answer)
		render '{ "response": "OK" }'
	}
		
	get("api/image/:id") {
		def id = DataService.getPathId(request.path)
		def file = new File("${FILE_STORAGE}/${id}.png")
		if (!file.exists()) {
			response.status(404) 	
			render '{ "response": "Not Found" }'
		} else {
		 	render file
		}
    }

	post("api/images/new") {
		def id = UUID.randomUUID().toString()
		def file = new File("${FILE_STORAGE}/${id}.png")
		file.withOutputStream { stream ->
			request.writeBodyTo(stream)
		}		
		render '{ "_id" : "' + id + '", "response": "OK" }'
	}

    assets "public"
	
  }
}


