X = dlmread('C:\\Users\\asus\\workspace\\SemSocWeb\\matr.txt', ',', 0, 1);
Z = linkage(X,'ward','cosine','savememory','on');
c = cluster(Z,'maxclust',50);



word = textread('C:\\Users\\asus\\workspace\\SemSocWeb\\tweetsNo.txt', '%s', 'delimiter', ',');


for i=1:size(unique(c))
    
    temp = word(c==i);
    fprintf('[%d]',i);
    for j = 1:length(temp)
        if j ~= length(temp)
            fprintf('%s,', temp{j});

        else
            fprintf('%s', temp{j});

        end
    end
    fprintf('\n');
    
end