clc
clear
X = dlmread('C:\\Users\\asus\\workspace\\SocSemWeb\\src\\matr.txt', ',', 0, 2);
sizeB = size(X, 1);
size__ = ceil(size(X, 1) / 10 * 7);


testData = X(size__ + 1:size(X, 1), :);
X = X(1:size__, :);

Z = linkage(X,'ward','cosine','savememory','on');
c = cluster(Z,'maxclust',25);



word = textread('C:\\Users\\asus\\workspace\\SocSemWeb\\src\\usersId.txt', '%s', 'delimiter', ',');


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


wordCnt = size__ + 1;

testCls = [];
for u = 1:size(testData, 1)
    rowTest = testData(u, :);
    
    max = -1;
    ind = 1;
    allInd = [];
    for i=1:length(unique(c))
        
        newX = X(c == i, :);
        for p = 1:size(newX, 1)
            temp = newX(p, :);
            if sum(rowTest .* temp) > max
                max = sum(rowTest .* temp);
                ind = i;
                
            end
        end
    end
    testCls = [testCls; strcat(word(wordCnt), ':', int2str(ind))];
    wordCnt = wordCnt + 1;
end
testCls

%..