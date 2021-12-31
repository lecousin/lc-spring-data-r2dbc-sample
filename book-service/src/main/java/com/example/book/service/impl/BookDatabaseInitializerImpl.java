package com.example.book.service.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.book.dao.model.Author;
import com.example.book.dao.model.Book;
import com.example.book.dao.model.Publisher;
import com.example.book.service.BookDatabaseInitializer;
import com.example.book.service.BookService;
import com.example.book.service.dto.AuthorDto;
import com.example.book.service.dto.BookDto;
import com.example.book.service.dto.PublisherDto;

import net.lecousin.reactive.data.relational.repository.LcR2dbcEntityTemplate;
import net.lecousin.reactive.data.relational.schema.RelationalDatabaseSchema;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BookDatabaseInitializerImpl implements BookDatabaseInitializer {

	@Autowired
	@Qualifier("bookOperations")
	private LcR2dbcEntityTemplate template;

	@Autowired
	private BookService service;
	
	@Override
	public Mono<Void> initSchema() {
		RelationalDatabaseSchema schema = template.getLcClient().buildSchemaFromEntities(Arrays.asList(Book.class, Author.class, Publisher.class));
		return template.getLcClient().dropCreateSchemaContent(schema);
	}
	
	@Override
	public Mono<Void> createAuthors() {
		return Flux.fromArray(new String[] {
			"John Smith", "William Miller", "Jessica Folk", "Joe Taylor", "Megan Davis", "David Pot", "Elisabeth Moore", "Michael Johnson", "Richard Brown", "Thomas Garcia",
			"James Rodriguez", "Robert Wilson", "Patricia Jackson", "Mary Martin", "Charles Thompson", "Christopher Harris", "Daniel Clark", "Matthew Robinson", "Anthony Lewis", "Mark Walker",
			"Donald Allen", "Steven Wright", "Paul Scott", "Andrew Hill", "Joshua Green", "Kenneth Adams", "Kevin Nelson", "Brian Baker", "George Campbell", "Edward Carter"
		}).map(AuthorDto::new).flatMap(service::createAuthor).then();
	}
	
	@Override
	public Mono<Void> createPublishers() {
		return Flux.fromArray(new String[] {
			"Summer Publications", "Spring Press", "Best Books", "Artisan Publishers", "Elite Books"
		}).map(PublisherDto::new).flatMap(service::createPublisher).then();
	}
	
	@Override
	public Mono<Void> createBooks() {
		return Mono.zip(service.getAuthors(null).collectList(), service.getPublishers(null).collectList())
			.flatMap(tuple -> {
				String[] bookNames = new String[] {
					"Absalom, Absalom!", "A che punto è la notte", "After Many a Summer Dies the Swan", "Ah, Wilderness!", "Alien Corn", "All Passion Spent", "All the King's Men",
					"Alone on a Wide, Wide Sea", "An Acceptable Time", "Antic Hay", "Arms and the Man", "As I Lay Dying", "Behold the Man", "Beneath the Bleeding", "Beyond the Mexique Bay",
					"Blithe Spirit", "Blood's a Rover", "Blue Remembered Earth", "Blue Remembered Hills", "Bonjour Tristesse", "Brandy of the Damned", "Bury My Heart at Wounded Knee",
					"Butter In a Lordly Dish", "By Grand Central Station I Sat Down and Wept", "Cabbages and Kings", "Captains Courageous", "Carrion Comfort", "A Catskill Eagle",
					"The Children of Men", "Clouds of Witness", "A Confederacy of Dunces", "Consider Phlebas", "Consider the Lilies", "Cover Her Face", "The Cricket on the Hearth",
					"The Curious Incident of the Dog in the Night-Time", "The Daffodil Sky", "Dance Dance Dance", "A Darkling Plain", "Death Be Not Proud", "The Doors of Perception",
					"Down to a Sunless Sea", "Down to a Sunless Sea", "Dying of the Light", "East of Eden", "Ego Dominus Tuus", "Endless Night", "Everything is Illuminated",
					"Eyeless in Gaza", "An Evil Cradling", "Fair Stood the Wind for France", "Fame Is the Spur", "A Fanatic Heart", "The Far-Distant Oxus", "A Farewell to Arms",
					"Far From the Madding Crowd", "Fear and Trembling", "For a Breath I Tarry", "For Whom the Bell Tolls", "Frequent Hearses", "From Here to Eternity",
					"The Getting of Wisdom", "A Glass of Blessings", "The Glory and the Dream", "The Golden Apples of the Sun", "The Golden Bowl", "Gone with the Wind",
					"The Grapes of Wrath", "Great Work of Time", "The Green Bay Tree", "A Handful of Dust", "Have His Carcase", "The Heart Is a Lonely Hunter",
					"The Heart Is Deceitful Above All Things", "His Dark Materials", "The House of Mirth", "How Sleep the Brave", "How Sleep the Brave", "How Sleep the Brave",
					"I Know Why the Caged Bird Sings", "I Sing the Body Electric!", "I Will Fear No Evil", "If I Forget Thee Jerusalem", "If Not Now, When?", "In a Dry Season",
					"In a Glass Darkly", "In Death Ground", "In Dubious Battle", "An Instant in the Wind", "It's a Battlefield", "Jacob Have I Loved", "O Jerusalem!", "Jesting Pilate",
					"The Last Enemy", "The Last Temptation", "The Lathe of Heaven", "Let Us Now Praise Famous Men", "Lilies of the Field", "This Lime Tree Bower", "The Line of Beauty",
					"The Little Foxes", "Little Hands Clapping", "A Little Learning", "Look Homeward, Angel", "Look to Windward", "The Man Within", "Many Waters", "A Many-Splendoured Thing",
					"The Mermaids Singing", "The Millstone", "The Mirror Crack'd from Side to Side", "Moab Is My Washpot", "The Monkey's Raincoat", "Monstrous Regiment",
					"A Monstrous Regiment of Women", "The Moon by Night", "Mother Night", "The Moving Finger", "The Moving Toyshop", "Mr Standfast", "Nectar in a Sieve", "The Needle's Eye",
					"Nine Coaches Waiting", "No Country for Old Men", "No Highway", "Noli Me Tangere", "No Longer at Ease", "Now Sleeps the Crimson Petal", "Number the Stars",
					"Of Human Bondage", "Of Mice and Men", "Oh! To be in England", "The Other Side of Silence", "The Painted Veil", "Pale Kings and Princes", "The Parliament of Man",
					"Paths of Glory", "A Passage to India", "O Pioneers!", "Postern of Fate", "Precious Bane", "The Proper Study", "Quo Vadis", "Recalled to Life", "Recalled to Life",
					"Ring of Bright Water", "The Road Less Traveled", "A Scanner Darkly", "Shall not Perish", "The Skull Beneath the Skin", "The Soldier's Art", "Some Buried Caesar",
					"Specimen Days", "The Stars' Tennis Balls", "Stranger in a Strange Land", "Such, Such Were the Joys", "A Summer Bird-Cage", "The Sun Also Rises", "Surprised by Joy",
					"A Swiftly Tilting Planet", "Taming a Sea Horse", "Tender Is the Night", "Terrible Swift Sword", "That Good Night", "That Hideous Strength", "Things Fall Apart",
					"This Side of Paradise", "Those Barren Leaves", "Thrones, Dominations", "Tiger! Tiger!", "A Time of Gifts", "Time of our Darkness", "A Time to Kill",
					"Time To Murder And Create", "Tirra Lirra by the River", "To a God Unknown", "To Sail Beyond the Sunset", "To Say Nothing of the Dog", "To Your Scattered Bodies Go",
					"The Torment of Others", "Unweaving the Rainbow", "Vanity Fair", "Vile Bodies", "The Violent Bear It Away", "Waiting for the Barbarians",
					"Wandering Recollections of a Somewhat Busy Life", "The Waste Land", "The Way of All Flesh", "The Way Through the Woods", "The Wealth of Nations",
					"What's Become of Waring", "When the Green Woods Laugh", "Where Angels Fear to Tread", "The Widening Gyre", "Wildfire at Midnight", "The Wind's Twelve Quarters",
					"The Wings of the Dove", "The Wives of Bath", "The World, the Flesh and the Devil", "The Yellow Meads of Asphodel"
				};
				int[] years = new int[] { 1970, 2010, 1654, 1875, 1986, 1994, 1964, 1945, 1897, 2003, 2020, 1981, 1983, 2005, 2007 };
				List<BookDto> books = new LinkedList<>();
				int index = 0;
				for (String bookName : bookNames) {
					BookDto book = new BookDto();
					book.setTitle(bookName);
					if ((index % (years.length + 1)) != years.length)
						book.setYear(years[index % (years.length + 1)]);
					book.setAuthors(new LinkedList<>());
					book.getAuthors().add(tuple.getT1().get(index % tuple.getT1().size()));
					if (index >= tuple.getT1().size() * 2)
						book.getAuthors().add(tuple.getT1().get((index + 1) % tuple.getT1().size()));
					if (index >= tuple.getT1().size() * 5)
						book.getAuthors().add(tuple.getT1().get((index + 2) % tuple.getT1().size()));
					if ((index % 12) != 11)
						book.setPublisher(tuple.getT2().get(index % tuple.getT2().size()));
					books.add(book);
					index++;
				}
				return Flux.fromIterable(books).flatMap(service::createBook).then();
			});
	}

}
