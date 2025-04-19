--- 
hide: -navigation -toc 
---

# Compose Unstyled

Unstyled, fully accessible components for Jetpack Compose & Compose Multiplatform that you can customize to your heart's content.

Available for 🖥️ Desktop, 🌐 Web (Js/WASM), 🤖 Android, 🍎 iOS, and any other platform Compose can run on.

<style>
  img {
    border-radius: 20px;
  }
  .image-grid {
    margin-top: 80px;
    display: grid;
    grid-template-columns: 1fr 1fr 1fr;
    gap: 20px;
    text-align: center;
  }
  @media (max-width: 900px) {
    .image-grid {
      grid-template-columns: 1fr 1fr;
    }
  }

  @media (max-width: 600px) {
    .image-grid {
      grid-template-columns: 1fr;
    }
  }
</style>

<div class="image-grid">
    <div>
        <a href="bottom-sheet">
          <img src="preview_bottomsheet.png" alt="Bottom Sheet Preview">
        </a>
        <div>Bottom Sheet</div>
    </div>

    <div>
        <a href="modal-bottom-sheet">
          <img src="preview_modalbottomsheet.png" alt="Bottom Sheet (Modal) Preview">
        </a>
        <div>Bottom Sheet (Modal)</div>
    </div>

  <div>
    <a href="dialog">
      <img src="preview_dialog.png" alt="Dialog Preview">
    </a>
    <div>Dialog</div>
  </div>

  <div>
    <a href="dropdown-menu">
      <img src="preview_menu.png" alt="Menu Preview">
    </a>
    <div>Dropdown Menu</div>
  </div>

  <div>
    <a href="icon">
      <img src="preview_icon.png" alt="Icon Preview">
    </a>
    <div>Icon</div>
  </div>

  <div>
      <a href="scrollarea">
        <img src="preview_scrollarea.png" alt="Scroll Area Preview">
      </a>
      <div>Scroll Area</div>
  </div>

  <div>
      <a href="separators">
        <img src="preview_separators.png" alt="Separators Preview">
      </a>
      <div>Separators</div>
  </div>
  <div>
      <a href="slider">
        <img src="preview_slider.png" alt="Slider Preview">
      </a>
      <div>Slider</div>
  </div>

    <div>
        <a href="tabgroup">
          <img src="preview_tab_group.png" alt="Tab Group Preview">
        </a>
        <div>Tab Group</div>
    </div>
</div>

## What developers say

<div class="testimonial-container">
  <div class="testimonial-image">
    <img src="/testimonials/matt.png" alt="Matt Kula">
  </div>
  <div class="testimonial-content">
    <div class="testimonial-text">
      "@alexstyl just wanted to let you know that we just transition from M3 Modal Bottom Sheet to your Compose Unstyled one and <a class="highlight" href="/modal-bottom-sheet">it fixed multiple issues 🎉</a>"
    </div>
    <div class="testimonial-author">
      <div>Matt Kula</div>
      <p>Android Engineer, Warner Music Group</p>
    </div>
  </div>
</div>

<div class="testimonial-container">
  <div class="testimonial-image">
    <img src="/testimonials/jacob.jpeg" alt="Jacob Ras">
  </div>
  <div class="testimonial-content">
    <div class="testimonial-text">
"Ever since Compose Unstyled was pointed out to me I use that one.
<a class="highlight" href="/modal-bottom-sheet">Simpler API, and it actually works.</a> I like it a lot, after continuously having something broken with the (Material Compose) bottom sheets."  
    </div>
    <div class="testimonial-author">
      <div>Jacob Ras</div>
      <p>Android Engineer, Albert Heijn</p>
    </div>
  </div>
</div>

<div class="testimonial-container">
  <div class="testimonial-image">
    <img src="/testimonials/isaac.jpg" alt="Isaac Zikstar">
  </div>
  <div class="testimonial-content">
    <div class="testimonial-text">
      "Thank you for making <a class="highlight" href="/modal-bottom-sheet">the only bottomsheet</a> composable library <a class="highlight" href="/modal-bottom-sheet">that actually makes sense!</a>"
    </div>
    <div class="testimonial-author">
      <div>Isaac Zikstar</div>
      <p>Android Engineer, Block</p>
    </div>
  </div>
</div>

<div class="testimonial-container">
  <div class="testimonial-image">
    <img src="/testimonials/gabor.jpg" alt="Gabor Varadi">
  </div>
  <div class="testimonial-content">
    <div class="testimonial-text">
"This man did, what Googlers couldn't in 3 years of <a class="highlight" href="/scrollarea">scrollbars</a> "being on the roadmap" #androiddev"  
    </div>
    <div class="testimonial-author">
      <div>Gabor Varadi</div>
      <p>EpicPandaForce</p>
    </div>
  </div>
</div>

<div class="testimonial-container">
  <div class="testimonial-image">
    <img src="/testimonials/alexstyl.jpg" alt="Alex Styl">
  </div>
  <div class="testimonial-content">
    <div class="testimonial-text">
      "I wrote this library, so this will be biased 😁. I was tired of dealing with Material Compose sheets and dialogs issues, <a class="highlight" href="/bottom-sheet">so I decided</a> <a class="highlight" href="/modal-bottom-sheet">to write my own</a> <a class="highlight" href="/dialog">from scratch</a>.
I also needed components that I can style according to my app needs instead of looking Material for my desktop apps, hence this library was born."
    </div>
    <div class="testimonial-author">
      <div>Alex Styl</div>
      <p>Author of Compose Unstyled</p>
    </div>
  </div>
</div>

<style>
     .highlight {
        background: #fce74c; 
        padding: 0 2px;
        border-radius: 3px;
        box-shadow: 0 0 5px rgba(252, 231, 76, 0.5); 
        color: black;
        font-weight: bold;
    }
    
    .highlight a {
        color: black;
        text-decoration: none; 
    }
  .testimonial-container {
    display: flex;
    max-width: 800px;
    font-family: Arial, sans-serif;
    padding-top: 8px;
    padding-bottom: 8px;
    flex-wrap: wrap;
  }
  .testimonial-image {
    flex: 0 0 192px;
    max-width: 192px;
    height: 192px;
  }
  .testimonial-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 10px; 
  }
  .testimonial-content {
    flex: 1;
    padding: 20px;
    display: flex;
    flex-direction: column;
  }
  .testimonial-text {
    font-size: 18px;
    margin: 0 0 20px 0;
    line-height: 1.6;
  }
  .testimonial-author h3 {
    margin: 0;
    font-size: 20px;
  }
  .testimonial-author p {
    margin: 5px 0 0;
    font-size: 16px;
    opacity: 0.8;
  }

  @media (max-width: 768px) {
    .testimonial-container {
      flex-direction: column;
      align-items: center;
    }
    .testimonial-image {
      max-width: 100px;
      height: 100px;
      flex: 0 0 100px;
    }
    .testimonial-content {
      padding: 10px;
    }
    .testimonial-text {
      font-size: 16px;
    }
    .testimonial-author h3 {
      font-size: 18px;
    }
    .testimonial-author p {
      font-size: 14px;
    }
  }

  :root {
    --background-color: #ffffff;
    --text-color: #000000;
  }
  @media (prefers-color-scheme: dark) {
    :root {
      --background-color: #1a1a2e;
      --text-color: #ffffff;
    }
  }
</style>


# Super simple migration

[Check out Gravatar's PR](https://github.com/Automattic/Gravatar-SDK-Android/pull/382) of migrating their Modal Bottom Sheets from Material Compose to [Compose Unstyled](/modal-bottom-sheet)