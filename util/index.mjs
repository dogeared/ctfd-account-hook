import * as cheerio from 'cheerio';

let session = process.argv[2];
let usersUrl = process.argv[3];


let fetchPage = async (page) => {
    let res = await fetch(`${usersUrl}?page=${page}`, {
        headers: {
            'Cookie': `session=${session}`
        }
    });
    let text = await res.text();
    return text;
}

(async () => {
    let page = 1;
    let users = [];
    let $ = cheerio.load(await fetchPage(page));
    while ($('table').length) {
        $('table tr').each((i, el) => {
            if (i === 0) return;
            let user = {};
            user.email = $(el).find('td.team-email').attr('value').trim();
            user.alias = $(el).find('td.team-name').text().trim();
            let nlIndex = user.alias.indexOf('\n');
            user.affiliation = user.alias.substring(nlIndex + 1).trim();
            user.alias = user.alias.substring(0, nlIndex);
            // $(el).find('td').each((j, td) => {
            //     let key = $(td).text().replace(':', '').trim().toLowerCase().replace(' ', '_');
            //     let value = $(td).next().text().trim();
            //     user[key] = value;
            // });
            users.push(user);
        });
        page++;
        $ = cheerio.load(await fetchPage(page));
    }
    users.forEach(user => console.log(`${user.email}, ${user.alias}, ${user.affiliation}`));
})();

