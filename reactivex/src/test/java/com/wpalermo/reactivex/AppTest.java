package com.wpalermo.reactivex;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

/**
 * Unit test for simple App.
 */
public class AppTest {

	private String result;

	@Test
	public void observableJust() {

		Observable<String> observable = Observable.just("Teste1");
		observable.subscribe(s -> result = s);

		assertTrue(result.equals("Teste1"));

	}

	
	@Test
	public void observableFrom() {

		String[] letters = { "a", "b", "c", "d", "e", "f", "g" };
		result = new String();

		Observable<String> observable = Observable.fromArray(letters);
		observable.subscribe(i -> result += i, 
						     Throwable::printStackTrace, 
						     () -> result += "_completed");

		assertTrue(result.equals("abcdefg_completed"));

	}
	
	
	@Test
	public void observableAsync() {

		String[] letters = { "a", "b", "c", "d", "e", "f", "g" };
		result = new String();

		Observable<String> observable = Observable.fromArray(letters).observeOn(Schedulers.io());
		observable.doOnNext(i -> {result += i; Thread.sleep(1000);});
//				  .subscribe(s -> result += "sub", 
//						  	 Throwable::printStackTrace,
//						  	 () -> completed(result));
					
		
		
		
		
		observable.doOnComplete(() -> completed());

		assertTrue(result.equals("abcdefg_completed"));

	}
	
	public void completed(String s) {
		System.out.println("Completadooo " + s);
	}
	
	/**
	 * Map -> aplica uma funcao alterando cada um dos opertators emitidos por um observable. (descartando o anterior) 
	 *  
	 */
	@Test
	public void observableFromTransformation() {

		String[] letters = { "a", "b", "c", "d", "e", "f", "g" };
		result = new String();

		Observable.fromArray(letters)
			.map(String::toUpperCase)
			.subscribe(letter -> result += letter);

		assertTrue(result.equals("ABCDEFG"));

	}
	
	/**
	 * SCAN -> aplica uma funcao alterando cada um dos opertators emitidos por um observable. 
	 * 			Porem, diferente do MAP, mantem o estado anterior permitindo que se carregue o estado atual para os proximos eventos/
	 */
	@Test
	public void observableFromScan() {

		String[] letters = { "a", "b", "c", "d", "e", "f", "g" };
		result = new String();
		
		Observable.fromArray(letters)
			.scan(new StringBuilder(), StringBuilder::append)
			.subscribe(letter -> result += letter);

		
		assertTrue(result.equals("aababcabcdabcdeabcdefabcdefg"));

	}
	
	
	private String pares;
	private String impares;
	
	@Test
	public void observableFromGroupBy() {

		Integer[] numbers = { 1, 2, 3, 4, 5, 6, 7 };
		result = new String();
		pares = new String();
		impares = new String();
		
		Observable.fromArray(numbers)
			.groupBy(key -> 0 == (key % 2) ? "PAR" : "IMPAR" )
			.subscribe(group -> 
					group.subscribe(number -> {
						if(group.getKey().toString().equals("PAR"))
							pares += number;
						else
							impares += number;
					}));
				
		assertTrue(pares.equals("246"));
		assertTrue(impares.equals("1357"));

		

	}


	
}
